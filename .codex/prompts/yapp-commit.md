---
description: YAPP Git Flow 규칙에 맞춰 변경 사항을 검증하고 커밋
argument-hint: '[ISSUE=번호] [TYPE=feature|fix|chore] [FILES="경로..."] [MESSAGE="커밋 메시지"]'
---

YAPP Android 저장소에서 커밋을 생성한다. 요청 인자: $ARGUMENTS

1. `git status`, 현재 브랜치, staged/unstaged diff를 확인한다. 원격 저장소가 `YAPP-admin/yapp-plus-android`인지 확인하고 `CONTRIBUTING.md`의 Git Flow 규칙을 따른다.
2. `main` 또는 `develop`에서 직접 커밋하지 않는다. `develop`에 있다면 이슈 번호와 작업 종류가 있을 때만 해당 작업 브랜치를 만들어 이동한다. 필요한 정보가 없으면 구체적으로 요청한다. `main`에 있다면 작업 목적에 맞는 기준 브랜치를 먼저 확인한다.
3. `FILES`가 지정되면 해당 파일만, 없으면 요청과 관련된 변경만 스테이징한다. 기존 staged 변경도 검사하고 비밀 정보, 빌드 산출물, 무관한 변경을 포함하지 않는다. 변경이 없으면 커밋을 만들지 않는다.
4. 코드 변경에는 적절한 빌드·테스트를 실행한다. Android 코드 변경은 기본적으로 `./gradlew :app:assembleDebug :app:lintDebug`를 실행한다. 실패하면 원인을 해결하거나 실패 내용을 보고하고 커밋을 중단한다.
5. `MESSAGE`가 있으면 의미를 유지해 사용하고, 없으면 변경 내용을 설명하는 `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:` 형식의 메시지를 작성한다. 관련 이슈가 있으면 커밋 본문에 `Refs #번호`를 적는다.
6. 커밋을 생성하고 커밋 SHA, 포함된 파일, 검증 결과, 남은 변경 사항을 보고한다. 사용자가 푸시까지 요청하지 않았다면 푸시하지 않는다.

충분한 정보가 있으면 추가 확인 없이 작업을 끝낸다. 기존 커밋을 수정하거나 강제 푸시하지 않는다.
