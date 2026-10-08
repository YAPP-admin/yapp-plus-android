"""Boundary and failure tests for the merge gate."""

from pathlib import Path
import sys
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from check_pr_policy import validate_pr


def pr(additions=500, deletions=300, labels=("feat",), title="출석 기능 흐름 추가"):
    return {
        "additions": additions,
        "deletions": deletions,
        "labels": [{"name": label} for label in labels],
        "title": title,
    }


class PrPolicyTests(unittest.TestCase):
    def test_800_is_allowed_including_deletion_only(self):
        for additions, deletions in [(800, 0), (500, 300), (0, 800)]:
            with self.subTest(additions=additions, deletions=deletions):
                self.assertEqual([], validate_pr(pr(additions, deletions)))

    def test_801_is_rejected_including_deletions(self):
        for additions, deletions in [(801, 0), (500, 301), (0, 801)]:
            with self.subTest(additions=additions, deletions=deletions):
                self.assertTrue(any("최대 800줄" in error for error in validate_pr(pr(additions, deletions))))

    def test_missing_or_invalid_counts_fail_closed(self):
        for count in [None, "800", -1, True]:
            with self.subTest(count=count):
                self.assertTrue(validate_pr(pr(additions=count)))

    def test_each_area_with_bug_or_maintenance_is_allowed(self):
        for area in ["ui/ux", "data", "domain", "feat"]:
            with self.subTest(area=area):
                self.assertEqual([], validate_pr(pr(labels=(area, "bug", "refactor"))))

    def test_development_area_label_names_are_case_insensitive(self):
        for area in ["UI/UX", "Data", "Domain", "Feat"]:
            with self.subTest(area=area):
                self.assertEqual([], validate_pr(pr(labels=(area,))))

    def test_maintenance_without_development_area_is_allowed(self):
        for labels in [("build",), ("refactor",), ("build", "refactor")]:
            with self.subTest(labels=labels):
                self.assertEqual([], validate_pr(pr(labels=labels)))

    def test_multiple_development_areas_fail_even_with_build(self):
        self.assertTrue(any("하나만" in error for error in validate_pr(pr(labels=("ui/ux", "feat", "build")))))

    def test_missing_area_and_legacy_labels_fail(self):
        for labels in [(), ("bug",), ("chore",), ("enhancement",), ("documentation",)]:
            with self.subTest(labels=labels):
                self.assertTrue(validate_pr(pr(labels=labels)))

    def test_invalid_label_response_fails_closed(self):
        for labels in [None, ["feat"], [{}]]:
            value = pr()
            value["labels"] = labels
            with self.subTest(labels=labels):
                self.assertTrue(validate_pr(value))

    def test_type_prefixes_are_rejected(self):
        for title in ["chore: 설정 추가", "feat(ui): 화면 추가", "fix!: 오류 수정", "[Refactor] 구조 정리", "docs - 정책 추가"]:
            with self.subTest(title=title):
                self.assertTrue(any("접두어" in error for error in validate_pr(pr(title=title))))

    def test_english_only_title_is_rejected_and_technical_names_are_allowed(self):
        self.assertTrue(validate_pr(pr(title="Add attendance flow")))
        self.assertEqual([], validate_pr(pr(title="GitHub Actions PR 정책 검사 추가")))


if __name__ == "__main__":
    unittest.main()
