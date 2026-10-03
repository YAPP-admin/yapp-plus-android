---
description: YAPP Git Flow 대상 브랜치에 Pull Request 생성
argument-hint: '[TITLE="제목"] [ISSUE=번호] [BASE=develop|main] [DRAFT=true|false]'
---

`YAPP-admin/yapp-plus-android`에 Pull Request를 생성한다. 요청 인자: $ARGUMENTS

1. `gh` 인증, 현재 브랜치, 작업 트리, 원격 저장소를 확인한다. `main`·`develop`에서 PR을 만들지 않는다. 미커밋 변경이 있으면 범위를 보고하고 커밋을 먼저 완료한다. 같은 head 브랜치의 열린 PR이 있으면 새로 만들지 않고 링크를 보고한다.
2. `CONTRIBUTING.md`에 따라 `feature/*`, `fix/*`, `chore/*`는 `develop`, `release/*`, `hotfix/*`는 `main`을 기본 대상으로 선택한다. 릴리즈·핫픽스의 `develop` 역병합 PR은 `BASE=develop`이 명시된 경우에만 만든다. 브랜치 패턴과 대상이 충돌하면 확인한다.
3. 기준 브랜치를 fetch하고 커밋·diff를 검토한다. 차이가 없으면 중단한다. Android 코드 변경은 `./gradlew :app:assembleDebug :app:lintDebug`를 실행하고 관련 테스트도 실행한다. 문서·설정 변경만 있으면 해당 파일의 형식과 내용으로 검증한다.
4. `.github/pull_request_template.md` 형식에 맞춰 변경 목적, 관련 이슈, 실제 검증 결과와 필요한 확인 사항을 쓴다. `develop` 대상 PR에서는 이슈 자동 종료를 가정하지 않고 `Related #번호`로 연결한다. 근거 없는 완료 표시를 하지 않는다.
5. 필요하면 현재 작업 브랜치를 원격에 푸시한 뒤 `gh pr create --repo YAPP-admin/yapp-plus-android --base ... --head ... --title ... --body-file ...`로 생성한다. 검증이 완료되지 않았거나 `DRAFT=true`면 Draft로 만들고, 검증이 완료되었으며 Draft 요청이 없으면 일반 PR로 만든다.
6. 생성된 PR을 다시 조회해 base·head·제목·Draft 상태를 확인한다. Codex의 PR 첨부 도구가 있으면 생성한 PR을 현재 작업에 첨부하고 URL과 검증 결과를 보고한다.

PR 병합과 브랜치 삭제는 이 명령의 범위가 아니다. 충분한 정보가 있으면 추가 확인 없이 생성한다.
