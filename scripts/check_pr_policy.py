#!/usr/bin/env python3
"""Validate a GitHub pull-request REST response against the YAPP PR policy."""

import argparse
import json
from pathlib import Path
import re
import sys

MAX_LINE_CHANGES = 800
DEVELOPMENT_LABELS = frozenset({"ui/ux", "data", "domain", "feat"})
MAINTENANCE_LABELS = frozenset({"build", "refactor"})
TYPES = r"build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test"
TYPE_PREFIX = re.compile(
    rf"^\s*(?:(?:{TYPES})(?:\([^)]*\))?!?\s*[:\-]|\[(?:{TYPES})(?:\([^]]*\))?\])",
    re.IGNORECASE,
)


def validate_pr(pr: dict) -> list[str]:
    errors = []
    counts = [pr.get("additions"), pr.get("deletions")]
    if any(type(count) is not int or count < 0 for count in counts):
        errors.append("추가·삭제 줄 수를 확인할 수 없습니다. PR REST 응답을 사용하세요.")
    elif sum(counts) > MAX_LINE_CHANGES:
        errors.append(f"추가 {counts[0]} + 삭제 {counts[1]} = {sum(counts)}줄: 최대 800줄로 PR을 나누세요.")

    title = pr.get("title")
    if not isinstance(title, str) or not re.search(r"[가-힣]", title):
        errors.append("PR 제목은 한국어로 작성하세요.")
    elif TYPE_PREFIX.match(title):
        errors.append("PR 제목에서 chore:, feat:, fix: 등의 유형 접두어를 제거하세요.")

    labels = pr.get("labels")
    if not isinstance(labels, list) or any(
        not isinstance(label, dict) or not isinstance(label.get("name"), str)
        for label in labels
    ):
        errors.append("PR 라벨을 확인할 수 없습니다. PR REST 응답을 사용하세요.")
    else:
        names = {label["name"].casefold() for label in labels}
        areas = names & DEVELOPMENT_LABELS
        if len(areas) > 1:
            errors.append("ui/ux·data·domain·feat별로 PR을 나누고 개발 영역 라벨 하나만 붙이세요.")
        elif not areas and not names & MAINTENANCE_LABELS:
            errors.append("개발 영역 라벨 하나 또는 유지보수용 build/refactor 라벨을 붙이세요.")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("pr_json", type=Path, help="JSON from gh api repos/OWNER/REPO/pulls/NUMBER")
    args = parser.parse_args()
    try:
        pr = json.loads(args.pr_json.read_text(encoding="utf-8"))
        if not isinstance(pr, dict):
            raise ValueError("PR 응답은 JSON 객체여야 합니다.")
    except (OSError, ValueError) as error:
        print(f"PR 응답을 읽을 수 없습니다: {error}", file=sys.stderr)
        return 1
    errors = validate_pr(pr)
    if errors:
        print("\n".join(errors), file=sys.stderr)
        return 1
    print(f"PR policy passed: +{pr['additions']} -{pr['deletions']} = {pr['additions'] + pr['deletions']}/800")
    return 0


if __name__ == "__main__":
    sys.exit(main())
