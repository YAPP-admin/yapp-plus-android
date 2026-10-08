# YAPP 출석 Android

[YAPP](https://www.yapp.co.kr/)의 공식 출석 앱 Android 프로젝트입니다.

## 현재 상태

9개 Gradle 모듈의 공통 설정은 `build-logic` convention plugin으로 관리합니다. Hilt 앱 진입점과 Ktor·Kotlin serialization 의존성, 공통 테마·인증 UI 표현·debug 전용 Preview 지원이 준비되어 있습니다. 로그인·회원가입 UI는 별도 모듈로 구현했고, 카카오 버튼 → 이름 → 휴대폰 번호 → 운영진 승인 대기의 UI 데모를 Navigation 2로 연결했습니다. 홈·일정·게시판·My의 정적 데모 화면을 공통 하단 탭으로 조합했습니다. 승인 대기의 “앱 둘러보기” 버튼은 App의 타입 기반 메인 목적지로 이동합니다. debug 빌드는 화면 캡처 편의를 위해 홈 탭으로 시작하고, release 빌드는 기존 카카오 로그인으로 시작합니다. 출석 기능과 실제 인증·API 연동은 아직 구현되지 않았습니다.

## 아키텍처와 모듈

MVVM + 단방향 상태 흐름(UDF)을 사용하고, 비즈니스 규칙은 필요한 범위의 Clean Architecture로 분리합니다.
현재 9개 Gradle 모듈과 build-logic 설정, 디자인시스템 공통 표현, `:core:preview`의 재사용 Preview 데이터가 준비되어 있습니다. 로그인은 `:feature:login`, 가입 입력·상태는 `:feature:signup`이 소유합니다. App은 Navigation Compose 2.10.2의 타입 기반 목적지로 데모 흐름을 조율하고, 이름·휴대폰 번호를 공통 ViewModel과 `SavedStateHandle`로 유지·복원합니다. 구현 상태와 모듈별 책임은 [Wiki 모듈 구조](docs/wiki/module-structure.md), 의존성 방향과 라이브러리 선택은 [Wiki 아키텍처](docs/wiki/architecture.md)에서 확인합니다. 출석 모델·정책·Repository·UI와 API 호출은 확정된 요구사항에 따라 구현합니다.

## 프로젝트 지식

[YAPP LLM Wiki](docs/wiki/README.md)는 앱 정책, 아키텍처, 설계 패턴, 코딩 규칙의 기준 문서입니다. 확정된 정책은 분야별 문서와 [결정 기록](docs/wiki/decisions.md)에 함께 반영합니다. 아직 공식화되지 않은 재사용 지식은 [후보함](docs/wiki/inbox/README.md)에서 사용 근거를 검토합니다. Codex의 세션 갱신 절차는 [AGENTS.md](AGENTS.md)에 있습니다.

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

전체 모듈 설정과 연결을 검증합니다.

```bash
./gradlew :domain:build assembleDebug lintDebug
```

단위 테스트는 다음 명령으로 실행합니다.

```bash
./gradlew :app:testDebugUnitTest
```

현재 `applicationId`와 네임스페이스는 `com.yapp.plus`입니다. 배포용 앱 ID는 Play Console 등록 전에 확정해야 합니다.

## 기여

브랜치 생성, PR 대상, 릴리즈 절차는 [기여 및 Git Flow 규칙](CONTRIBUTING.md)을 따릅니다.

모든 PR의 추가·삭제 줄 수 합은 **800 이하**로 제한합니다. 개발 PR은 `ui/ux`·`data`·`domain`·`feat`별로 나누고 영역 라벨 하나를 붙입니다. `PR policy` 필수 검사가 변경량, 라벨, 한국어 제목과 유형 접두어 사용 여부를 확인합니다.

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
| `/prompts:yapp-pr` | 대상 브랜치·800줄 제한·영역 확인·PR 생성 | `/prompts:yapp-pr ISSUE=1 AREA=build TITLE="Codex 슬래시 커맨드 추가"` |

설치된 파일을 원본에 맞춰 갱신하려면 `python3 scripts/install_codex_prompts.py --force`를 실행합니다. 사용자 정의 프롬프트는 명령을 실행할 때 현재 세션의 모델을 자동으로 바꾸지 않습니다.

OpenAI Docs는 사용자 정의 프롬프트를 더 이상 권장하지 않지만, 명시적인 `/prompts:<이름>` 명령을 위해 이 형식을 사용합니다. Codex가 명령을 읽지 못하면 새 채팅에서 다시 시도하거나 Codex를 재시작합니다.

## 프로젝트 개발 스킬

검토·승인한 1~35번 규칙은 Wiki에 보관하고 `.agents/skills/`에서 작업별로 연결합니다. [AGENTS.md](AGENTS.md)의 안내에 따라 적용하거나 아래 이름으로 요청할 수 있습니다.

| 스킬 | 적용 범위 |
| --- | --- |
| [$yapp-architecture](.agents/skills/yapp-architecture/SKILL.md) | 계층 책임·의존성 방향 |
| [$yapp-coding-conventions](.agents/skills/yapp-coding-conventions/SKILL.md) | Kotlin 서식·가시성·함수·이름·리소스 |
| [$yapp-design-patterns](.agents/skills/yapp-design-patterns/SKILL.md) | Route/Content·상태·요청·Repository·화면 이동 |
| [$yapp-module-structure](.agents/skills/yapp-module-structure/SKILL.md) | 파일·패키지 배치와 모듈 추가·검증 |

[.editorconfig](.editorconfig)에 기본 서식과 Kotlin 최대 줄 길이 100자를 기록했습니다.
포맷터와 정적 분석 플러그인은 별도로 선택합니다.
