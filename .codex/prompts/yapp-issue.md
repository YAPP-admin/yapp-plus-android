---
description: YAPP 템플릿과 라벨을 사용해 GitHub 이슈 생성
argument-hint: '[TYPE=bug|feature|chore] [TITLE="제목"] [DETAILS="내용"] [LABELS="라벨..."]'
---

`YAPP-admin/yapp-plus-android`에 GitHub 이슈를 생성한다. 요청 인자: $ARGUMENTS

1. `gh` 인증과 저장소를 확인하고, 요청 내용을 바탕으로 이슈 유형과 제목을 정한다. 제목이나 핵심 설명이 없으면 그 정보만 요청한다. 추측으로 재현 단계나 완료 기준을 채우지 않는다.
2. 열린 이슈와 닫힌 이슈에서 중복을 검색한다. 같은 문제가 이미 있다면 링크를 제시하고 중복 이슈는 만들지 않는다.
3. `.github/ISSUE_TEMPLATE`의 필드에 맞춰 본문을 작성한다. 버그는 현상·재현 방법·기대 동작·환경, 기능은 문제·제안·완료 기준, 유지보수는 목표·완료 기준을 사용한다. 제공되지 않은 선택 항목은 생략한다.
4. `bug`, `enhancement`, `chore`, `documentation` 중 맞는 유형 라벨을 붙인다. `LABELS`가 있으면 실제 존재하는 라벨인지 확인하고 함께 붙인다. 우선순위는 사용자가 명시했거나 근거가 충분할 때만 지정한다.
5. 본문을 임시 UTF-8 파일에 저장하고 `gh issue create --repo YAPP-admin/yapp-plus-android --title ... --body-file ... --label ...`로 생성한다. 생성 후 `gh issue view`로 제목·본문·라벨을 확인하고 URL을 보고한다.

개인정보나 비밀 값이 포함된 로그는 이슈에 올리기 전에 제거한다. 충분한 정보가 있으면 추가 확인 없이 생성한다.
