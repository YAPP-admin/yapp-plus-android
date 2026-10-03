---
description: YAPP 템플릿과 라벨을 사용해 GitHub 이슈 생성
argument-hint: '[TYPE=bug|feature|chore] [AREA=ui/ux|data|domain|feat|build|refactor] [TITLE="제목"] [DETAILS="내용"] [LABELS="라벨..."]'
---

`YAPP-admin/yapp-plus-android`에 GitHub 이슈를 생성한다. 요청 인자: $ARGUMENTS

1. `gh` 인증과 저장소를 확인하고, 요청 내용을 바탕으로 이슈 유형과 제목을 정한다. 제목이나 핵심 설명이 없으면 그 정보만 요청한다. 추측으로 재현 단계나 완료 기준을 채우지 않는다.
2. 열린 이슈와 닫힌 이슈에서 중복을 검색한다. 같은 문제가 이미 있다면 링크를 제시하고 중복 이슈는 만들지 않는다.
3. `.github/ISSUE_TEMPLATE`의 필드에 맞춰 본문을 작성한다. 버그는 현상·재현 방법·기대 동작·환경, 기능은 문제·제안·완료 기준, 유지보수는 목표·완료 기준을 사용한다. 제공되지 않은 선택 항목은 생략한다.
4. `CONTRIBUTING.md`의 영역 정의에 따라 `AREA` 또는 요청에서 `ui/ux`, `data`, `domain`, `feat`, `build`, `refactor` 라벨을 정한다. `TYPE=feature`는 기본 `feat`, `TYPE=chore`는 작업에 따라 `build` 또는 `refactor`이며 브랜치/커밋 유형을 그대로 라벨로 만들지 않는다. 버그는 `bug`와 해당 영역을 함께 붙인다. `gh label list`로 실제 라벨을 조회하고 `LABELS`도 존재 여부를 확인한다. `enhancement`, `chore`, `documentation`, `priority:*` 등 이전 라벨을 사용하거나 재생성하지 않는다.
5. 여러 개발 영역에 걸친 작업은 본문에 ui/ux·data·domain·feat별 PR 계획, 순서와 완료 기준을 적는다. 각 PR은 추가 줄 수 + 삭제 줄 수 800 이하로 계획한다. 범위가 독립적이면 이슈도 영역별로 나누고 서로 연결한다.
6. 본문을 임시 UTF-8 파일에 저장하고 `gh issue create --repo YAPP-admin/yapp-plus-android --title ... --body-file ... --label ...`로 생성한다. 생성 후 `gh issue view`로 제목·본문·라벨을 확인하고 URL을 보고한다.

개인정보나 비밀 값이 포함된 로그는 이슈에 올리기 전에 제거한다. 충분한 정보가 있으면 추가 확인 없이 생성한다.
