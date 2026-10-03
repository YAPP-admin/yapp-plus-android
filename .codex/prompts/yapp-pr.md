---
description: YAPP Git Flow 대상 브랜치에 Pull Request 생성
argument-hint: '[TITLE="제목"] [ISSUE=번호] [AREA=ui/ux|data|domain|feat|build|refactor] [BASE=develop|main] [DRAFT=true|false]'
---

`YAPP-admin/yapp-plus-android`에 Pull Request를 생성한다. 요청 인자: $ARGUMENTS

1. `gh` 인증, 현재 브랜치, 작업 트리, 원격 저장소를 확인한다. `main`·`develop`에서 PR을 만들지 않는다. 미커밋 변경이 있으면 범위를 보고하고 커밋을 먼저 완료한다. 같은 head 브랜치의 열린 PR이 있으면 새로 만들지 않고 링크를 보고한다.
2. `CONTRIBUTING.md`에 따라 `feature/*`, `fix/*`, `chore/*`는 `develop`, `release/*`, `hotfix/*`는 `main`을 기본 대상으로 선택한다. 릴리즈·핫픽스의 `develop` 역병합 PR은 `BASE=develop`이 명시된 경우에만 만든다. 브랜치 패턴과 대상이 충돌하면 확인한다.
3. 기준 브랜치를 fetch하고 커밋·diff를 검토한다. 차이가 없으면 중단한다. `git diff --numstat origin/<대상>...HEAD`의 추가·삭제 줄 수를 모두 합산한다. 파일 제외 없이 800 이하여야 하며, 바이너리의 `-`는 줄 수에 포함하지 않는다. 800줄을 넘거나 ui/ux·data·domain·feat 변경이 섞여 있으면 PR 생성과 푸시를 중단하고 영역·의존 순서·800줄 제한에 맞는 별도 브랜치/PR 분리안을 제시한다. 커밋만 나누거나 Draft로 만들어 제한을 우회하지 않는다. 릴리즈·핫픽스에도 같은 제한을 적용한다.
4. `CONTRIBUTING.md`의 영역 정의에 따라 개발 PR에는 `ui/ux`, `data`, `domain`, `feat` 중 정확히 하나를 선택한다. `feat`로 여러 영역을 합치지 않는다. 유지보수는 `build`/`refactor`, 버그 수정은 해당 영역에 `bug`를 추가한다. `AREA`가 diff와 맞는지 확인하고 `gh label list`로 실제 라벨 존재 여부를 검증한다. Android 코드 변경은 `./gradlew :app:assembleDebug :app:lintDebug`와 관련 테스트를 실행한다. 문서·설정 변경은 해당 파일의 형식과 내용으로 검증한다.
5. PR 제목은 변경 내용을 한국어로 작성한다. `chore:`, `feat:`, `fix:` 같은 유형 접두어를 붙이지 않는다. `TITLE`에 접두어가 있거나 영어 문장으로 주어져도 의미를 유지하며 이 규칙에 맞게 고친다. 기술명과 고유명사는 원래 표기를 사용할 수 있다.
6. `.github/pull_request_template.md` 형식에 맞춰 변경 목적, 영역 라벨, 추가·삭제 줄 수 합, 선행·후속 PR, 관련 이슈, 실제 검증 결과를 쓴다. `develop` 대상 PR에서는 이슈 자동 종료를 가정하지 않고 `Related #번호`로 연결한다. 근거 없는 완료 표시를 하지 않는다.
7. 필요하면 현재 작업 브랜치를 원격에 푸시한 뒤 `gh pr create --repo YAPP-admin/yapp-plus-android --base ... --head ... --title ... --body-file ... --label ...`로 영역 라벨과 함께 생성한다. 검증이 완료되지 않았거나 `DRAFT=true`면 Draft로 만들고, 검증이 완료되었으며 Draft 요청이 없으면 일반 PR로 만든다.
8. 생성된 PR을 `gh api repos/YAPP-admin/yapp-plus-android/pulls/<번호>`로 조회해 JSON을 임시 파일에 저장하고 `python3 scripts/check_pr_policy.py <파일>`로 GitHub가 집계한 변경량·제목·라벨을 검증한다. 실패하면 성공으로 보고하지 않고 PR을 나누거나 제목·라벨을 수정한다. `gh pr checks <번호>`로 필수 검사 상태도 확인하며 대기 중인 검사를 통과로 보고하지 않는다. base·head·제목·라벨·Draft 상태를 다시 확인한다. Codex의 PR 첨부 도구가 있으면 생성한 PR을 현재 작업에 첨부하고 URL과 검증 결과를 보고한다.

PR 병합과 브랜치 삭제는 이 명령의 범위가 아니다. 충분한 정보가 있으면 추가 확인 없이 생성한다.
