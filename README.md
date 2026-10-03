# YAPP 출석 Android

[YAPP](https://www.yapp.co.kr/)의 공식 출석 앱 Android 프로젝트입니다.

## 현재 상태

Jetpack Compose 기반의 단일 `:app` 모듈과 시작 화면이 준비되어 있습니다. 출석 기능, 인증, 서버 연동은 아직 구현되지 않았습니다.

## 개발 환경

- Android Studio와 Android SDK 37
- JDK 25 (Gradle daemon JVM 설정으로 자동 선택)
- Kotlin 2.2.10, Android Gradle Plugin 9.5.0-alpha02, Gradle 9.6.0
- minSdk 24, targetSdk 37

Android Studio에서 프로젝트 루트를 열고 Gradle 동기화를 실행합니다. 로컬 SDK 경로는 `local.properties`에 설정하며 이 파일은 Git에서 제외됩니다.

## 빌드

```bash
./gradlew :app:assembleDebug
```

단위 테스트가 추가되면 다음 명령으로 실행합니다.

```bash
./gradlew :app:testDebugUnitTest
```

현재 `applicationId`와 네임스페이스는 `com.yapp.plus`입니다. 배포용 앱 ID는 Play Console 등록 전에 확정해야 합니다.

## 기여

브랜치 생성, PR 대상, 릴리즈 절차는 [기여 및 Git Flow 규칙](CONTRIBUTING.md)을 따릅니다.

## Codex 슬래시 커맨드

이 저장소의 [Codex 설정](.codex/config.toml)은 새 세션의 모델을 `gpt-6-luna`, 추론 수준을 `max`로 지정합니다. 프로젝트를 신뢰한 Codex 세션에서 적용되며, 세션에서 명시적으로 선택한 모델이 있으면 그 선택이 우선합니다.

커맨드 원본은 `.codex/prompts/`에 보관합니다. Codex의 사용자 정의 프롬프트는 사용자 홈의 `prompts` 폴더에서 로드하므로, 저장소를 받은 뒤 다음 명령으로 설치하고 새 채팅을 열거나 Codex를 다시 시작합니다.

```bash
python3 scripts/install_codex_prompts.py
```

| 커맨드 | 용도 | 예시 |
| --- | --- | --- |
| `/prompts:yapp-commit` | 변경 검토·검증·커밋 | `/prompts:yapp-commit ISSUE=1 TYPE=chore` |
| `/prompts:yapp-issue` | 중복 확인·이슈 생성 | `/prompts:yapp-issue TYPE=bug TITLE="로그인 오류" DETAILS="재현 방법..."` |
| `/prompts:yapp-pr` | 대상 브랜치 확인·검증·PR 생성 | `/prompts:yapp-pr ISSUE=1` |

설치된 파일을 원본에 맞춰 갱신하려면 `python3 scripts/install_codex_prompts.py --force`를 실행합니다. 사용자 정의 프롬프트는 명령을 실행할 때 현재 세션의 모델을 자동으로 바꾸지 않습니다.

OpenAI Docs는 사용자 정의 프롬프트를 더 이상 권장하지 않지만, 명시적인 `/prompts:<이름>` 명령을 위해 이 형식을 사용합니다. Codex가 명령을 읽지 못하면 새 채팅에서 다시 시도하거나 Codex를 재시작합니다.
