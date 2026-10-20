# YAPP 출석 Android

[YAPP](https://www.yapp.co.kr/)의 공식 출석 앱 Android 프로젝트입니다.

## 현재 상태

13개 Gradle 모듈의 공통 설정은 `build-logic` convention plugin으로 관리합니다. Hilt 앱 진입점과 Ktor 공통 API 클라이언트, JSON 직렬화와 DataStore 기반 YAPP 세션 저장·복원이 준비되어 있고, 이번 주 세션·현재 기수 일정 조회를 Domain·Data·홈/일정 Feature·App까지 연결했습니다. 로그인·회원가입 UI는 별도 모듈이며, Google Credential Manager에서 ID 토큰과 nonce를 발급해 App 콜백에 전달합니다. App이 ID 토큰·원본 nonce를 Supabase Auth에 교환한 뒤에만 로그인 완료로 처리합니다. 홈·게시판·My·일정은 각각 `:feature:main:home`, `:feature:main:board`, `:feature:main:my`, `:feature:main:schedule`로 분리했고, App이 네 화면과 메인 탭 내비게이션·선택 상태를 관리합니다. 가입 UI 데모는 승인 대기에서 끝나며, 승인 대기 화면에는 하단 액션을 제공하지 않습니다. 홈은 이번 주 세션 API를 표시하고 출석 기록 API 연동 전까지 최근 출석 이력을 표시하지 않으며, My 화면은 데모 데이터를 표시합니다. debug·release 모두 시작 시 Supabase 세션 복원을 기다리고 인증 여부에 따라 메인 또는 Google 로그인 화면을 표시합니다. My API, YAPP 초기 로그인 세션 발급과 회원 정보·출석 기록 API는 아직 연동되지 않았습니다.

인증 세션이 공급된 뒤의 HTTP 401 자동 갱신, 원래 요청 1회 재시도와 동시 갱신 중복 방지를 구현했습니다. Data가 갱신 API·DTO·로컬 저장 연결을, Network가 토큰 쌍 복원·교체와 인증 처리를 소유합니다. `:core:datastore`가 Keystore로 암호화한 토큰 쌍을 백업 제외 경로에 저장하며 API 환경별로 분리합니다. 초기 YAPP 로그인 API 연동은 후속이며 저장된 세션이 없으면 별도의 유효한 YAPP 토큰 쌍 공급이 필요합니다.

비공개 MVP의 Supabase Auth 어댑터·암호화 세션 저장과 일정·공지·출석 검증 Data를 App Hilt에 연결했습니다. 일정은 테스트 세션만 조회하고 공식 기수가 비어 있으면 테스트 전용 0으로 매핑합니다. 출석은 인증된 RPC로 네 자리 코드를 보내고 서버가 확정한 결과를 검증합니다. MVP migration에는 보고 backend가 없어 해당 호출은 일반 실패로 끝납니다. Login Feature의 Supabase 교환·시작 시 세션 복원·오류 재시도를 연결했습니다. 홈의 주간 공지와 게시판의 유형별 목록·상세를 Repository에 연결했습니다. 보고 UI 정리는 후속이며 DB migration은 실제 프로젝트에 아직 적용하지 않았습니다.

## 아키텍처와 모듈

인증된 API의 401 갱신과 원래 요청 1회 재시도, 동시 갱신 공유를 구현했습니다. Data의 갱신 API와 DTO, DataStore 토큰 저장소를 App singleton으로 조립하며, `:core:datastore`는 토큰 쌍을 암호화해 저장·복원합니다. 초기 YAPP 로그인 세션 발급은 후속입니다. 저장소 프로세스 재시작 검증 방법은 [모듈 구조](docs/wiki/module-structure.md#인증-저장소-자동-검증)에 있습니다.

MVVM + 단방향 상태 흐름(UDF)을 사용하고, 비즈니스 규칙은 필요한 범위의 Clean Architecture로 분리합니다.
현재 13개 Gradle 모듈과 build-logic 설정, 디자인시스템 공통 표현, `:core:preview`의 재사용 Preview 데이터가 준비되어 있습니다. 로그인은 `:feature:login`, 가입 입력·상태는 `:feature:signup`이 소유합니다. App은 Navigation Compose 2.10.2의 타입 기반 목적지와 Google ID 토큰 콜백을 조립하고, 메인 탭 내비게이션과 선택 상태도 소유합니다. 이름·휴대폰 번호를 공통 ViewModel과 `SavedStateHandle`로 유지·복원합니다. 구현 상태와 모듈별 책임은 [Wiki 모듈 구조](docs/wiki/module-structure.md), 의존성 방향과 라이브러리 선택은 [Wiki 아키텍처](docs/wiki/architecture.md)에서 확인합니다. 출석 모델·정책·Repository·UI와 API 호출은 확정된 요구사항에 따라 구현합니다.

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


### 수정된 홈 화면

홈의 주간 세션·달력·빠른 메뉴, 알림 UI, 출석 코드 입력/완료와 출결 보고 흐름을 수정된 Figma에 맞춰 연결합니다. 출석 검증은 Supabase RPC를 사용하고 기존 YAPP 검증·보고 어댑터는 향후 전환을 위해 보존합니다. 현재 Supabase 경로에서 보고는 지원하지 않으며 홈의 주간 공지와 게시판 목록·상세는 Repository에 연결했으며 알림·출석 기록 조회는 후속입니다. 지각 시간의 추가 선택은 디자이너 확인 전 임시 적용입니다. 상태와 적용 범위는 [제품 정책](docs/wiki/product-policy.md), 작업 기록은 [#82](https://github.com/YAPP-admin/yapp-plus-android/issues/82)를 참고합니다.
