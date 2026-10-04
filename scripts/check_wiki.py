"""Validate YAPP Wiki links, authority, decisions, and candidate evidence."""

from datetime import date
from pathlib import Path
import re
import sys


LINK = re.compile(r"(?<!!)\[[^]]+\]\(([^)]+)\)")
DECISION = re.compile(r"^## (DEC-\d{4})\b", re.MULTILINE)
CONTEXTS = {"plan", "implementation", "test", "review"}


def frontmatter(path):
    content = path.read_text(encoding="utf-8")
    if not content.startswith("---\n"):
        return {}, content
    _, header, body = content.split("---", 2)
    fields = dict(re.findall(r"^([a-z_]+):\s*(.*)$", header, re.MULTILINE))
    return fields, body


def check_links(paths):
    errors = []
    for path in paths:
        for target in LINK.findall(path.read_text(encoding="utf-8")):
            if target.startswith(("https://", "http://", "mailto:", "#")):
                continue
            local = target.split("#", 1)[0]
            if local and not (path.parent / local).exists():
                errors.append(f"{path}: broken link {target}")
    return errors


def candidate_uses(body):
    section = body.split("## 실제 사용 이력", 1)
    if len(section) != 2:
        return None
    rows = []
    for line in section[1].split("## ", 1)[0].splitlines():
        if not line.startswith("|"):
            continue
        cells = [cell.strip() for cell in line.strip("|").split("|")]
        if cells[0] in {"작업 ID 또는 이슈", "---"}:
            continue
        rows.append(cells)
    return rows


def check_candidate(path, decisions, root):
    fields, body = frontmatter(path)
    errors = []
    required = {"type", "authority", "status", "created_at", "updated_at", "use_count", "last_used_at", "target_path", "resolution_reason"}
    if required - fields.keys():
        return [f"{path}: missing fields {', '.join(sorted(required - fields.keys()))}"]
    if fields["type"] != "knowledge-candidate" or fields["authority"] != "none":
        errors.append(f"{path}: candidate must have type knowledge-candidate and authority none")
    status = fields["status"]
    if status not in {"candidate", "promoted", "rejected"}:
        errors.append(f"{path}: invalid status {status}")
    try:
        for key in ("created_at", "updated_at"):
            date.fromisoformat(fields[key])
        count = int(fields["use_count"])
    except ValueError:
        return errors + [f"{path}: invalid date or use_count"]
    rows = candidate_uses(body)
    if rows is None:
        return errors + [f"{path}: missing actual-use section"]
    tasks = set()
    dates = []
    for row in rows:
        if len(row) != 4 or not all(row) or row[2] not in CONTEXTS:
            errors.append(f"{path}: invalid actual-use row {row}")
            continue
        try:
            dates.append(date.fromisoformat(row[1]))
        except ValueError:
            errors.append(f"{path}: invalid use date {row[1]}")
        if row[0] in tasks:
            errors.append(f"{path}: duplicate task {row[0]}")
        tasks.add(row[0])
    if count != len(tasks):
        errors.append(f"{path}: use_count must equal distinct actual-use tasks")
    expected_last = max(dates).isoformat() if dates else "null"
    if fields["last_used_at"] != expected_last:
        errors.append(f"{path}: last_used_at must be {expected_last}")
    target = fields["target_path"]
    reason = fields["resolution_reason"]
    if status == "promoted":
        match = re.search(r"^- 결정 ID: (DEC-\d{4})$", body, re.MULTILINE)
        if not match or match.group(1) not in decisions:
            errors.append(f"{path}: promoted candidate needs a recorded decision ID")
        target_page = root / target
        if not target.startswith("docs/wiki/") or not target_page.is_file() or reason == "null":
            errors.append(f"{path}: promoted candidate needs reason and target page")
        elif frontmatter(target_page)[0].get("authority") != "canonical":
            errors.append(f"{path}: promotion target must be canonical")
    elif status == "rejected":
        if reason == "null" or target != "null":
            errors.append(f"{path}: rejected candidate needs reason and no target")
    elif target != "null" or reason != "null":
        errors.append(f"{path}: unresolved candidate cannot have resolution or target")
    return errors


def validate(root):
    wiki = root / "docs/wiki"
    candidate_files = [path for path in (wiki / "inbox").glob("*.md") if path.name != "README.md"]
    templates = list((wiki / "templates").glob("*.md"))
    canonical = [path for path in wiki.rglob("*.md") if path not in candidate_files + templates]
    docs = [*canonical, *candidate_files, *templates, root / "AGENTS.md", root / "README.md", *root.glob(".agents/skills/*/SKILL.md")]
    errors = check_links(docs)
    for path in canonical:
        fields, _ = frontmatter(path)
        if fields.get("authority") != "canonical" or fields.get("status") != "active":
            errors.append(f"{path}: canonical page needs active canonical metadata")
    decision_text = (wiki / "decisions.md").read_text(encoding="utf-8")
    ids = DECISION.findall(decision_text)
    if len(ids) != len(set(ids)):
        errors.append("docs/wiki/decisions.md: duplicate decision ID")
    for path in candidate_files:
        errors.extend(check_candidate(path, set(ids), root))
    return errors


if __name__ == "__main__":
    repository = Path(__file__).resolve().parents[1]
    problems = validate(repository)
    if problems:
        print("\n".join(problems), file=sys.stderr)
        sys.exit(1)
    print("Wiki validation passed")
