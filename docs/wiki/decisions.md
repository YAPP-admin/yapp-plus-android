---
authority: canonical
status: active
last_verified: 2026-10-08
---

# 결정 기록

확정된 정책의 변경 이유를 날짜순으로 남깁니다. 현재 적용 규칙은 분야별 문서에서 읽습니다. 새 결정에는 다음 `DEC-0013`부터 순서대로 ID를 부여하고, 이전 결정을 대체하면 해당 ID를 기록합니다. 이전 항목은 삭제하거나 현재 규칙처럼 수정하지 않습니다.

## DEC-0001 · 기존 승인 규칙을 Wiki의 초기 기준으로 이전

- 기록일: 2026-10-04 (기존 결정의 원래 확정일이 아닌 이전 작업일)
- 결정: 승인된 아키텍처, 모듈, 설계 패턴, 코딩 규칙을 분야별 Wiki 문서의 초기 기준으로 삼습니다.
- 이유: 에이전트 작업 지침과 실제 프로젝트 규칙을 분리하고 중복을 줄입니다.
- 근거: [이슈 #3](https://github.com/YAPP-admin/yapp-plus-android/issues/3), [PR #4](https://github.com/YAPP-admin/yapp-plus-android/pull/4), 기존 `AGENTS.md`와 프로젝트 스킬.
- 반영: [아키텍처](architecture.md), [모듈 구조](module-structure.md), [설계 패턴](design-patterns.md), [코딩 컨벤션](coding-conventions.md), [제품 정책](product-policy.md).
- 대체한 결정: 없음.

## DEC-0002 · 세션 정책 갱신과 지식 후보 승격 절차 도입

- 기록일: 2026-10-04
- 결정: 명확히 확정된 정책은 해당 Wiki와 이 기록에 같은 작업에서 반영합니다. 근거가 부족한 반복 지식은 inbox 후보로 보관하고 사용 근거를 검토한 뒤 승격합니다.
- 이유: 세션에서 생긴 장기 지식의 누락을 줄이고 후보를 공식 정책으로 오인하지 않도록 합니다.
- 근거: 이번 세션의 사용자 요청, [이슈 #5](https://github.com/YAPP-admin/yapp-plus-android/issues/5), [참고 저장소의 Wiki 유지보수 흐름](https://github.com/chanho0908/Keepiluv-Agent/blob/3e570985bf6561e59f419380f4d4ea8514c24ee7/wiki/schema/maintenance.md).
- 반영: [Wiki 운영](README.md), [후보함](inbox/README.md), `AGENTS.md`.
- 대체한 결정: 없음.

## DEC-0003 · Figma 타이포그래피를 앱 디자인시스템의 기준으로 적용

- 기록일: 2026-10-05
- 결정: YAPP Figma Typography 페이지의 25개 텍스트 스타일, Pretendard JP 글꼴과 `"ss10" 1` 글꼴 기능을 `:core:designsystem`의 기준으로 적용합니다. Material 3 타이포그래피 슬롯은 이 토큰으로 연결합니다.
- 이유: 화면에서 반복 가능한 글자 스타일을 앱 코드에서 공유하고, 디자인과 구현의 수치를 일치시킵니다.
- 근거: [이슈 #9](https://github.com/YAPP-admin/yapp-plus-android/issues/9), [YAPP Figma Typography](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-50739), 사용자 확인.
- 반영: [디자인시스템](design-system.md), `:core:designsystem`의 `YappTypography`와 `YappTheme`.
- 대체한 결정: 없음.

## DEC-0004 · 공통 컴포넌트 색상 토큰 중앙화

- 기록일: 2026-10-06
- 결정: 공통 디자인시스템 컴포넌트의 고정 색상은 `:core:designsystem/theme/Color.kt`의 `YappColor` 토큰으로 관리하고 컴포넌트와 Preview에서 재사용합니다. 기존 ARGB·알파값은 유지합니다.
- 이유: 동일 색상의 컴포넌트별 중복 선언을 없애고 디자인 변경을 한 곳에서 반영합니다.
- 근거: 이번 세션의 사용자 요청, [이슈 #13](https://github.com/YAPP-admin/yapp-plus-android/issues/13)의 Figma Components 구현 범위.
- 반영: [디자인시스템](design-system.md), `:core:designsystem`의 `theme/Color.kt`, PR #14·#16·#18·#19.
- 대체한 결정: 없음.

## DEC-0005 · Gradle convention plugin, Hilt와 Ktor 구성

- 기록일: 2026-10-06
- 결정: 공통 빌드 설정은 `build-logic` included build의 convention plugin으로 관리합니다. Android DI는 Hilt, HTTP client는 Ktor Android engine을 사용하고 JSON 직렬화는 Kotlin serialization을 사용합니다. Hilt 컴파일은 KSP로 처리합니다.
- 이유: 모듈마다 중복된 SDK·JVM·Compose 설정을 한 곳에서 유지하고, 사용자 요청에 따라 앱 DI와 네트워크 기술을 확정합니다. 프로젝트 Kotlin 2.2.10과 호환되는 Ktor 3.3.3 및 kotlinx.serialization 1.9.0을 선택합니다. Ktor 3.3.3의 version catalog는 Kotlin 2.2.21, serialization 1.9.0은 Kotlin 2.2.0을 기준으로 합니다.
- 근거: 사용자 요청, [이슈 #20](https://github.com/YAPP-admin/yapp-plus-android/issues/20), [Gradle convention plugin 안내](https://docs.gradle.org/current/userguide/best_practices_structuring_builds.html), [Ktor 3.3.3 버전 카탈로그](https://github.com/ktorio/ktor/blob/3.3.3/gradle/libs.versions.toml), [Kotlin serialization 1.9.0 릴리스](https://github.com/Kotlin/kotlinx.serialization/releases/tag/v1.9.0), [Hilt 공식 설정](https://developer.android.com/training/dependency-injection/hilt-android), [Dagger 2.60.1 릴리스](https://github.com/google/dagger/releases/tag/dagger-2.60.1).
- 반영: [아키텍처](architecture.md), [모듈 구조](module-structure.md), `AGENTS.md`, `README.md`, `settings.gradle.kts`, `build-logic/`, `gradle/libs.versions.toml`.
- 적용 상태: Convention plugin과 App Hilt 진입점, Ktor·Kotlin serialization 의존성을 설정했습니다. API별 HttpClient 사용, DTO와 Repository 구현은 서버 계약이 없어 후속입니다. AGP 9의 내장 Kotlin에서 KSP가 generated source를 등록하도록 `android.disallowKotlinSourceSets=false`를 설정했습니다 ([KSP #2729](https://github.com/google/ksp/issues/2729)).
- 대체한 결정: 없음.

## DEC-0006 · ktlint와 Android CI 검사 도입

- 기록일: 2026-10-06
- 결정: Kotlin과 Gradle Kotlin DSL 서식 검사에는 ktlint Gradle plugin 14.2.0을 사용하고 PR CI에서 `./gradlew ktlintCheck`, `./gradlew test`, `./gradlew :app:assembleDebug :app:bundleRelease`를 실행합니다.
- 이유: CI에서 코드 서식, 기존 Android·Domain 단위 테스트, 앱 Debug APK와 Release bundle을 함께 검증합니다. 저장소의 앱 모듈은 `:app`이며 `testAndroidHostTest` 태스크는 없습니다. `./gradlew test`가 현재 모듈들의 단위 테스트 태스크를 실행합니다.
- 근거: 사용자 요청, [이슈 #22](https://github.com/YAPP-admin/yapp-plus-android/issues/22), [ktlint Gradle plugin 14.2.0](https://plugins.gradle.org/plugin/org.jlleitschuh.gradle.ktlint/14.2.0).
- 반영: [코딩 컨벤션](coding-conventions.md), `gradle/libs.versions.toml`, `build.gradle.kts`, `.github/workflows/android-ci.yml`.
- 적용 상태: `:app`과 `:core:designsystem`의 적용 전 위반을 각 `ktlint-baseline.xml`에 기록했습니다. `ktlintCheck`는 기준 파일에 없는 신규 위반을 계속 검사합니다.
- 대체한 결정: 없음.

## DEC-0007 · opt-in Astra 계획·6.1 SOL 구현 흐름

- 기록일: 2026-10-07
- 결정: 사용자가 `/yapp-work`를 호출한 경우에만 `gpt-6-astra` 서브에이전트가 읽기 전용 계획을 작성합니다. 사용자의 명시적 승인 뒤 `gpt-6.1-sol` 서브에이전트가 승인된 계획을 구현합니다. 두 단계는 추론 수준 `max`로 직렬 실행하며, 기본 모델과 다른 작업의 흐름은 변경하지 않습니다.
- 이유: 계획 검토와 구현을 모델 역할별로 분리하면서, 승인 전에 저장소나 GitHub 상태가 바뀌지 않도록 합니다.
- 근거: 사용자 요청 및 승인, [이슈 #24](https://github.com/YAPP-admin/yapp-plus-android/issues/24).
- 반영: `AGENTS.md`, `.codex/prompts/yapp-work.md`.
- 대체한 결정: 없음.

## DEC-0008 · 로그인·회원가입용 공용 UI 변형

- 기록일: 2026-10-07
- 결정: 승인된 Figma의 로그인·회원가입 표현을 버튼·입력창·팝업의 명시적 변형과 별도 색상 토큰으로 제공합니다. 기존 공용 기본값을 유지하며 화면 상태·검증·인증 연동과 로고 리소스는 Feature가 소유합니다.
- 이유: 기존 공용 표현과 다른 색상·크기·정렬을 보존하면서 후속 인증 화면에서 같은 UI 계약을 재사용합니다.
- 근거: UI만 구현하고 카카오 제공자를 사용하는 사용자 승인, [이슈 #26](https://github.com/YAPP-admin/yapp-plus-android/issues/26), [Figma 로그인·회원가입](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487).
- 반영: [디자인시스템](design-system.md), [공통 컴포넌트](design-system-components.md), `:core:designsystem`.
- 적용 상태: 공용 UI 변형을 추가했습니다. 화면과 앱 진입점 연결은 [이슈 #27](https://github.com/YAPP-admin/yapp-plus-android/issues/27)에서 진행합니다.
- 대체한 결정: 없음.

## DEC-0009 · 인증 공통 표현과 독립 Preview 기반

- 기록일: 2026-10-07
- 결정: 인증의 재사용 가능한 표현을 Designsystem에서 제공하고 시스템 뒤로 가기·전체 화면 이동은 App이 소유합니다. 범용 Preview provider·입력 fixture는 독립된 `:core:preview`의 debug 소스에 두며 소비 모듈도 debug 의존성으로만 연결합니다. 새로 만들거나 이동·수정하는 모든 UI 컴포넌트에는 개별 Preview를 제공하고, 코드에 드러난 내용을 반복하는 불필요한 주석·KDoc을 추가하지 않습니다. PR에 첨부하는 스크린샷은 `<img src="..." width="300" />`으로 표시합니다.
- 이유: 후속 로그인·회원가입 모듈에서 공통 표현과 Preview 데이터를 재사용하면서 기능 상태·목적지·업무 정책의 경계를 유지합니다.
- 근거: 이번 세션의 사용자 승인, [이슈 #33](https://github.com/YAPP-admin/yapp-plus-android/issues/33), 후속 화면 작업 [이슈 #27](https://github.com/YAPP-admin/yapp-plus-android/issues/27).
- 반영: [코딩 컨벤션](coding-conventions.md), [모듈 구조](module-structure.md), [설계 패턴](design-patterns.md), [공통 컴포넌트](design-system-components.md), `README.md`, `.codex/prompts/yapp-work.md`, `.codex/prompts/yapp-pr.md`.
- 적용 상태: 공통 표현 5개와 각각의 Preview, 범용 Preview 지원 모듈을 제공합니다. 로그인·회원가입 모듈 분리와 화면 연결은 기반 PR 병합 뒤 후속 작업에서 구현합니다. Hilt 객체 조립 문구는 DEC-0005의 확정 내용과 실제 코드에 맞춰 동기화했습니다.
- 대체한 결정: 없음. DEC-0008의 기능 상태·검증·인증 연동·카카오 로고의 Feature 소유는 유지합니다.

## DEC-0010 · 로그인 시작점과 회원가입 진입 요소 제거

- 기록일: 2026-10-07
- 결정: 앱 초기 화면은 카카오 로그인 UI로 제공합니다. 회원가입 안내 문구·버튼과 “또는” 구분선은 표시하지 않습니다.
- 이유: 사용자가 로그인 화면의 회원가입 진입 요소와 구분선을 제거하도록 확정했습니다.
- 근거: 사용자 명시 요청 및 승인, [이슈 #27](https://github.com/YAPP-admin/yapp-plus-android/issues/27), [Figma 로그인·회원가입](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487).
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `:feature:login`과 앱 초기 화면.
- 적용 상태: 로그인·가입 UI를 별도 모듈로 제공하고 앱은 로그인 화면으로 시작합니다.
- 대체한 결정: 없음.

## DEC-0011 · Navigation 2 기반 가입 UI 데모 경로 승인

- 기록일: 2026-10-07
- 결정: 카카오 버튼 → 이름 입력 → 휴대폰 번호 입력 → 운영진 승인 대기의 UI 데모 경로를 Navigation 2로 연결합니다. 실제 인증·가입 서버 처리는 포함하지 않습니다.
- 이유: 화면 표현을 독립 모듈로 분리한 뒤 App에서 승인된 데모 흐름을 연결하기 위해 단계별 PR로 진행합니다.
- 근거: 이번 세션의 사용자 승인, [이슈 #27](https://github.com/YAPP-admin/yapp-plus-android/issues/27).
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `README.md`.
- 적용 상태: 모듈 분리 PR #29 병합 후 [이슈 #35](https://github.com/YAPP-admin/yapp-plus-android/issues/35)에서 Navigation Compose 2.10.2의 타입 기반 목적지와 App의 공통 ViewModel·`SavedStateHandle`로 데모 흐름을 구현했습니다. 뒤로 가기·반복 탭·입력값 유지·복원을 연결하고 입력 검증 없이 다음 버튼을 활성화합니다. 완료·문제 상태는 Preview 전용이며 실제 인증·가입 서버 처리는 후속입니다.
- 대체한 결정: 없음.

## DEC-0012 · 승인 대기 화면에서 메인 탭 UI 데모로 진입

- 기록일: 2026-10-08
- 결정: 승인 대기 화면의 “앱 둘러보기” 동작으로 홈·일정·게시판·My 탭을 가진 정적 UI 데모에 진입합니다. 실제 기기 스크린샷을 위한 임시 홈 시작 경로는 debug 변형에만 추가하고 release의 기존 로그인 진입 흐름을 유지합니다. 탭 화면의 회원·일정·출석 표시는 실제 사용자 데이터나 서버 확정 결과가 아닙니다.
- 이유: 인증 서버가 없는 상태에서 승인 대기 이후의 메인 화면과 하단 탭을 확인할 수 있도록 합니다.
- 근거: 사용자 요청 및 [이슈 #37](https://github.com/YAPP-admin/yapp-plus-android/issues/37), [Figma 아이콘 모음](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-57816).
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `README.md`, `:feature:attendance`.
- 적용 상태: 정적 탭 화면과 공통 하단 내비게이션 어댑터, 탭 선택·복원, 승인 대기 상태의 버튼·콜백을 구현했습니다. App의 타입 기반 메인 목적지 연결과 debug 전용 홈 시작 경로는 후속 `feat` PR에 남아 있습니다. 실제 인증·출석 API와 정책은 포함하지 않습니다.
- 대체한 결정: 없음.
