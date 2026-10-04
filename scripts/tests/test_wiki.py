import tempfile
import unittest
from pathlib import Path

from scripts.check_wiki import check_candidate


CANDIDATE = """---
type: knowledge-candidate
authority: none
status: {status}
created_at: 2026-10-04
updated_at: 2026-10-04
use_count: {count}
last_used_at: {last_used}
target_path: null
resolution_reason: {reason}
---
## 실제 사용 이력
| 작업 ID 또는 이슈 | 사용일 | 맥락 | 실제 판단 근거 |
| --- | --- | --- | --- |
{uses}"""


class WikiValidationTest(unittest.TestCase):
    def check_candidate_text(self, **values):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            path = root / "candidate.md"
            path.write_text(CANDIDATE.format(**values), encoding="utf-8")
            return check_candidate(path, set(), root)

    def test_same_task_cannot_raise_use_count(self):
        problems = self.check_candidate_text(
            status="candidate", count=2, last_used="2026-10-04", reason="null",
            uses="| issue-1 | 2026-10-04 | plan | 경계 결정 |\n"
            "| issue-1 | 2026-10-04 | review | 같은 작업 재사용 |\n",
        )
        self.assertTrue(any("duplicate task" in problem for problem in problems))
        self.assertTrue(any("use_count" in problem for problem in problems))

    def test_promotion_needs_decision_and_target(self):
        problems = self.check_candidate_text(
            status="promoted", count=0, last_used="null", reason="승격", uses="",
        )
        self.assertTrue(any("decision ID" in problem for problem in problems))
        self.assertTrue(any("target page" in problem for problem in problems))
