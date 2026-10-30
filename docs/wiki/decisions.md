---
authority: canonical
status: active
last_verified: 2026-10-30
---

# 결정 기록

확정된 정책의 변경 이유를 날짜순으로 남깁니다. 현재 적용 규칙은 분야별 문서에서 읽습니다. 새 결정에는 다음 `DEC-0046`부터 순서대로 ID를 부여하고, 이전 결정을 대체하면 해당 ID를 기록합니다. 이전 항목은 삭제하거나 현재 규칙처럼 수정하지 않습니다.

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
- 적용 상태: Convention plugin과 App Hilt 진입점, Ktor 공통 HttpClient·Kotlin serialization 및 debug 개발 토큰 공급을 구현했습니다. API별 호출, DTO와 Repository 구현은 서버 계약이 없어 후속입니다. AGP 9의 내장 Kotlin에서 KSP가 generated source를 등록하도록 `android.disallowKotlinSourceSets=false`를 설정했습니다 ([KSP #2729](https://github.com/google/ksp/issues/2729)).
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
- 적용 상태: 모듈 분리 PR #29 병합 후 [이슈 #35](https://github.com/YAPP-admin/yapp-plus-android/issues/35)에서 Navigation Compose 2.10.2의 타입 기반 목적지와 App의 공통 ViewModel·`SavedStateHandle`로 데모 흐름을 구현했습니다. 뒤로 가기·반복 탭·입력값 유지·복원을 연결하고 입력 검증 없이 다음 버튼을 활성화합니다. 현재 개발 버전의 입력·인증 연결 범위는 DEC-0037을 따릅니다.
- 대체한 결정: 현재 개발 버전의 인증 연결 범위는 DEC-0037에서 정합니다.

## DEC-0012 · 승인 대기 화면에서 메인 탭 UI 데모로 진입

- 기록일: 2026-10-08
- 결정: 승인 대기 화면의 “앱 둘러보기” 동작으로 홈·일정·게시판·My 탭을 가진 정적 UI 데모에 진입합니다. 실제 기기 스크린샷을 위한 임시 홈 시작 경로는 debug 변형에만 추가하고 release의 기존 로그인 진입 흐름을 유지합니다. 탭 화면의 회원·일정·출석 표시는 실제 사용자 데이터나 서버 확정 결과가 아닙니다.
- 이유: 인증 서버가 없는 상태에서 승인 대기 이후의 메인 화면과 하단 탭을 확인할 수 있도록 합니다.
- 근거: 사용자 요청 및 [이슈 #37](https://github.com/YAPP-admin/yapp-plus-android/issues/37), [Figma 아이콘 모음](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-57816).
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `README.md`, `:feature:attendance`, `:app`.
- 적용 상태: 정적 탭 화면과 공통 하단 내비게이션, 탭 선택·복원, 승인 대기에서 App의 타입 기반 메인 목적지로 이동하는 흐름을 [PR #42](https://github.com/YAPP-admin/yapp-plus-android/pull/42)까지 구현했습니다. 초안의 고유 표현인 홈의 데모 출석 이력과 홈·일정·My의 데모 데이터 안내도 연결했습니다. debug는 홈 탭으로 시작하고 release는 카카오 로그인으로 시작합니다. 실제 기기 캡처는 후속 작업입니다. 실제 인증·출석 API와 정책은 포함하지 않습니다.
- 대체한 결정: 없음.

## DEC-0013 · 승인 대기 화면의 메인 데모 진입 버튼 제거

- 기록일: 2026-10-12
- 결정: 승인 대기 화면의 “앱 둘러보기” 버튼을 제거하고 가입 UI 데모는 승인 대기에서 끝납니다. 메인 탭 확인을 위한 debug 홈 시작 경로는 유지합니다.
- 이유: 사용자 요청에 따라 승인 대기 화면의 하단 액션을 제거해 현재 Figma 표현과 맞춥니다.
- 근거: 이번 세션의 사용자 명시 요청, [이슈 #44](https://github.com/YAPP-admin/yapp-plus-android/issues/44), [Figma 승인 대기](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=222-40229).
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `README.md`, `:feature:signup`, `:app`.
- 적용 상태: 승인 대기 액션·문자열과 App의 메인 이동 콜백을 제거하고 가입 흐름·입력 복원 기기 테스트를 갱신했습니다. 완료·문제 상태의 액션은 제공합니다.
- 대체한 결정: DEC-0012의 승인 대기에서 메인 탭으로 진입하는 결정. 정적 탭 데모와 debug 홈 시작 경로는 유지합니다. 현재 개발 버전의 가입 연결 상태는 DEC-0037을 따릅니다.

## DEC-0014 · 2026 Notion 기반 회원·출석 정책을 Wiki에 반영

- 기록일: 2026-10-18
- 결정: 정관·운영세칙 ver 6.0 (2026)을 기준으로 한 회원 구분, 앱 권한 역할과의 차이, 기수·직군 용어, 출석 점수·상태와 확인된 화면 정책을 Wiki의 기준으로 기록합니다. 출처가 확인 필요·미정·개선안으로 표시한 내용은 미확정으로 유지합니다.
- 이유: 기존 제품 정책은 출석 판정 기준을 미확정으로 두었지만, 최신 문서에서 점수·상태와 화면 동작의 기준 및 근거가 확인되었습니다. 동시에 경계값·일부 대상 자격과 화면 제안은 출처에서 미결정으로 표시하므로 확정 규칙과 분리합니다.
- 근거: [화면 및 기능별 정책 2026](https://app.notion.com/p/yapp-workspace/2026-3fa2106a0e84800f9abfef181b090892), [용어 정의서 2026](https://app.notion.com/p/yapp-workspace/2026-3fa2106a0e84802e8e37d0e9ffee3fb9). 두 문서는 YAPP 정관·운영세칙 ver 6.0 (2026)을 출처로 명시합니다.
- 반영: [회원·역할 정책](membership-policy.md), [제품 정책](product-policy.md), [Wiki 목차](README.md).
- 적용 상태: 앱 화면은 기존 UI 데모 상태입니다. 출석·회원 자격 판정이나 인증·가입 API는 연결하지 않았으며, 출석 경계와 서버 계약을 제품 정책의 미확정 항목으로 유지합니다.
- 대체한 결정: 없음.

## DEC-0015 · 메인 하단 탭을 독립 Feature 모듈로 분리

- 기록일: 2026-10-18
- 결정: BottomNavigation의 홈·일정·게시판·My 탭을 각각 `:feature:home`, `:feature:schedule`, `:feature:board`, `:feature:my` 모듈로 분리합니다. App은 네 Feature를 조립하고 메인 내비게이션과 탭 선택 상태를 소유하며, 공통 하단 탭 시각 컴포넌트는 `:core:designsystem`에 둡니다. Feature끼리 직접 의존하지 않습니다.
- 이유: 메인 화면의 네 기능을 독립된 Feature 경계로 관리하면서 기존 App 중심 화면 이동과 공통 디자인시스템 책임을 유지합니다.
- 근거: 사용자 명시 결정, [이슈 #62](https://github.com/YAPP-admin/yapp-plus-android/issues/62). App의 전체 내비게이션 소유와 Feature 간 비의존 원칙은 기존 [아키텍처](architecture.md)를 적용했습니다.
- 반영: [아키텍처](architecture.md), [모듈 구조](module-structure.md), [디자인시스템](design-system.md), `AGENTS.md`, `README.md`, App 및 Feature 모듈.
- 적용 상태: 탭 내비게이션과 선택 상태는 App으로 이동했습니다. 화면 분리 경로는 DEC-0016에서 `:feature:main` 아래로 재정의했으며, 네 탭을 독립 모듈로 옮기고 기존 `:feature:attendance`를 제거했습니다.
- 대체한 결정: 없음.

## DEC-0016 · 메인 탭 Feature 모듈 경로를 `feature:main` 아래로 구성

- 기록일: 2026-10-18
- 결정: 홈·일정·게시판·My를 각각 `:feature:main:home`, `:feature:main:schedule`, `:feature:main:board`, `:feature:main:my`로 둡니다. App은 메인 내비게이션과 선택 상태를 소유하고 네 모듈을 조립합니다. Feature 간 직접 의존성은 두지 않으며 공통 메인 헤더는 `:core:designsystem`에서 제공합니다.
- 이유: 네 탭의 독립 Feature 경계를 `:feature:main` 아래에 모아 표현하고 공통 UI와 App 조립 책임을 명확히 합니다.
- 근거: 사용자의 명시 요청, [이슈 #62](https://github.com/YAPP-admin/yapp-plus-android/issues/62), App으로 탭 셸을 이동한 [PR #64](https://github.com/YAPP-admin/yapp-plus-android/pull/64).
- 반영: [아키텍처](architecture.md), [모듈 구조](module-structure.md), `AGENTS.md`, `README.md`, `:feature:main:board`, `:core:designsystem`.
- 적용 상태: 게시판 화면과 분류 상태를 `:feature:main:board`에, My 화면과 컴포넌트를 `:feature:main:my`에, 일정 화면·상태·모델·리소스를 `:feature:main:schedule`에, 홈 화면·상태·모델·컴포넌트·리소스를 `:feature:main:home`에 분리했습니다. App이 네 화면과 탭 선택을 조립하고 기존 `:feature:attendance`는 제거했습니다. 공통 헤더·데모 안내·아이콘 라벨은 `YappMainHeader`, `YappDemoDataLabel`, `YappIconLabel`로 Designsystem에 둡니다.
- 대체한 결정: DEC-0015의 개별 탭 모듈 경로만 대체합니다. App 소유의 메인 내비게이션과 선택 상태, 공통 하단 탭 표현, Feature 간 비의존 원칙은 유지합니다.

## DEC-0017 · Google 로그인으로 ID 토큰 발급

- 기록일: 2026-10-19
- 결정: 로그인 화면에는 Google 로그인 버튼만 표시합니다. 버튼은 Credential Manager로 Google ID 토큰과 임의 nonce를 받아 메모리 콜백으로 App에 전달합니다. 서버 검증과 YAPP 세션 발급 계약이 없으므로 ID 토큰을 발급해도 로그인 완료나 가입 데모 진입으로 처리하지 않습니다. 로그·영구 저장은 하지 않습니다.
- 이유: 사용자가 카카오 버튼을 Google 로그인으로 교체한 뒤 Cloud OAuth 클라이언트 설정과 Android ID 토큰 발급까지 요청했고, 서버 연결은 ID 토큰 발급까지만으로 범위를 확정했습니다.
- 근거: 사용자 요청 및 범위 확인, [이슈 #79](https://github.com/YAPP-admin/yapp-plus-android/issues/79), [Figma 로그인](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487), Google Cloud `yapp-plus` 프로젝트 External/Testing 설정.
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `README.md`, `:feature:login`, App 인증 콜백.
- 적용 상태: `com.yapp.plus`와 debug SHA-1 Android OAuth 클라이언트, ID 토큰 audience Web OAuth 클라이언트를 구성했습니다. 현재 release 서명 지문은 없어 등록하지 않았습니다. 앱은 기본 Google 프로필 범위의 ID 토큰과 nonce를 획득하지만 서버 검증·YAPP 세션 발급은 미구현입니다. Testing 상태이므로 등록된 테스트 사용자만 로그인할 수 있습니다.
- 대체한 결정: DEC-0010의 로그인 제공자와 DEC-0011의 데모 진입 제공자만 대체합니다. 가입 UI 요소 제거와 가입 화면의 표시·입력 상태는 유지하되 Google ID 토큰만으로 진입하지 않습니다.

## DEC-0018 · 커밋과 PR 작업은 명시 요청이 있을 때만 수행

- 기록일: 2026-10-19
- 결정: 구현 요청은 필요한 이슈 작성, 구현, 검증과 리뷰까지 진행합니다. 커밋과 PR 생성·갱신은 사용자가 해당 작업을 명시적으로 요청한 경우에만 수행합니다. 푸시는 PR 생성·갱신에 필요한 경우에만 합니다.
- 이유: 구현 완료를 커밋이나 PR로 공유하라는 요청으로 간주하지 않도록 합니다.
- 근거: 사용자의 명시 요청, [이슈 #88](https://github.com/YAPP-admin/yapp-plus-android/issues/88).
- 반영: `AGENTS.md`, `.codex/prompts/yapp-work.md`.
- 적용 상태: 두 작업 지침의 실행 범위, 조율자 책임과 완료 절차에 반영했습니다.
- 대체한 결정: 없음. 기존 에이전트 작업 지침을 변경합니다.

## DEC-0019 · 401 세션 갱신과 동시 갱신 중복 방지

- 기록일: 2026-10-19
- 결정: 인증된 API 요청의 401은 refresh token으로 갱신하고 원래 요청을 한 번 재시도합니다. 동시에 전송된 요청은 갱신의 성공·실패를 공유해 refresh token 회전 API의 중복 호출을 막습니다.
- 이유: 여러 화면의 API가 같은 만료 토큰으로 요청해도 동일한 refresh token을 반복 사용하지 않도록 합니다.
- 근거: 이번 세션의 사용자 명시 요청, [이슈 #76](https://github.com/YAPP-admin/yapp-plus-android/issues/76), 개발 서버 OpenAPI의 `POST /api/auth/session-renewals` 계약.
- 반영: [설계 패턴](design-patterns.md), [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `README.md`, Network·Data 및 App 조립.
- 적용 상태: Network의 세션별 갱신 공유와 401 재시도 기반을 구현했습니다. Data 갱신 API 연결과 App DI는 후속 PR입니다.
- 대체한 결정: 없음.

## DEC-0020 · 홈 화면 정적 출석 이력 데모 제거

- 기록일: 2026-10-19
- 결정: 홈 화면에 고정된 최근 출석 이력, 데모 배지와 데모 데이터 안내를 표시하지 않습니다. 출석 기록 API가 연동되기 전까지 홈은 이번 주 세션 조회 API의 상태만 표시합니다. 세션의 예시 값은 debug 전용 Preview에서만 사용합니다.
- 이유: 홈의 데모 코드를 제거해 실제 출석 기록이 없는 상태에서 정적 이력이 사용자 화면에 나타나지 않게 합니다.
- 근거: 이번 세션의 사용자 명시 요청과 현재 홈의 API 연결 상태.
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), `README.md`, `:feature:main:home`.
- 적용 상태: 정적 이력 카드와 데모 안내를 제거하고, 무상태 화면의 데모 세션 대체값과 Preview 전용 값을 분리했습니다.
- 대체한 결정: DEC-0012의 홈 데모 출석 이력·안내 부분만 대체합니다. debug 홈 시작 경로와 다른 화면의 데모 UI는 유지합니다.

## DEC-0021 · 의존성 주입과 UI 컴포넌트 패키지 분리

- 기록일: 2026-10-19
- 결정: Android 모듈의 Hilt 의존성 주입 선언은 모듈 루트의 `di/` 패키지에 둡니다. 독립 UI 컴포넌트는 `components/` 패키지에 두고 Route·Screen과 분리합니다. 새 컴포넌트 Preview도 `src/debug`에서 같은 `components/` 패키지 경로를 사용합니다.
- 이유: 의존성 조립과 UI 컴포넌트의 위치를 모듈마다 일관되게 찾아 관리합니다.
- 근거: 사용자의 명시 요청.
- 반영: [모듈 구조](module-structure.md), [디자인시스템 공통 컴포넌트](design-system-components.md).
- 적용 상태: Designsystem과 메인 Feature의 기존 컴포넌트 패키지 및 debug Preview를 `components/`로 옮기고 참조를 갱신했습니다. 로그인 오류 다이얼로그도 Feature의 `components/`로 분리했습니다. 기존 Hilt 의존성 주입 파일은 이미 `di/`에 있어 그대로 유지합니다.
- 대체한 결정: 없음.

## DEC-0022 · 함수는 단일 책임을 갖도록 분리

- 기록일: 2026-10-19
- 결정: 함수는 단일 책임 원칙을 따릅니다. 요청 구성, 외부 호출, 응답 검증·변환처럼 변경 이유가 다른 작업을 책임별 함수로 분리하고, 상위 함수는 흐름을 조립합니다.
- 이유: 함수가 변경되는 이유와 실패 지점을 좁혀 구현·리뷰·검증을 쉽게 합니다.
- 근거: 사용자의 명시 요청과 `getGoogleLoginCredential`의 책임 혼합 지적.
- 반영: [코딩 컨벤션](coding-conventions.md), `:feature:login`의 Google Credential Manager 구현.
- 적용 상태: `getGoogleLoginCredential`은 nonce와 로그인 결과를 조립하고, Credential Manager 요청 구성·실행, credential 검증과 ID 토큰 파싱은 각각 별도 함수가 담당합니다.
- 대체한 결정: 없음.

## DEC-0023 · 수정된 홈 디자인과 출석·출결 보고 흐름

- 기록일: 2026-10-19
- 결정: 수정된 Figma 홈 메인과 알림·출석 입력/완료·사전 보고 화면을 반영하고, 확인된 출석 인증·보고 API를 연결합니다. 서버 성공 뒤에만 완료 UI를 표시하며 조회 계약이 없는 공지·알림 예시는 debug Preview에만 둡니다.
- 이유: 홈에서 이어지는 전체 화면을 반영하라는 사용자 요청과 실제 API의 상태·일회성 변경 결과를 일치시키기 위함입니다.
- 근거: 사용자 확정, [Figma 홈](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=222-40284), [이슈 #82](https://github.com/YAPP-admin/yapp-plus-android/issues/82), 개발 서버 OpenAPI와 backend dev의 출석 검증·보고 계약.
- 반영: [제품 정책](product-policy.md), [모듈 구조](module-structure.md), [공통 컴포넌트](design-system-components.md), [디자인시스템](design-system.md).
- 적용 상태: 홈·연결 화면 UI와 API 호출·입력·제출 상태를 구현합니다. 지각은 사용자 지시에 따라 1시간/3시간 선택 단계를 우선 적용하며 디자이너 확인 뒤 조정합니다. 알림·공지·출석 기록 조회와 프로세스 종료 뒤 완료 상태 복원은 후속입니다.
- 대체한 결정: DEC-0020의 출석 이력 데모 제거를 유지하면서 홈 레이아웃과 연결 흐름만 확장합니다. 출석 판정·점수 정책을 새로 확정하지 않습니다.

## DEC-0024 · DataStore 기반 YAPP 세션 저장과 복원

- 기록일: 2026-10-20
- 결정: 서버가 발급·회전한 YAPP access/refresh token 쌍을 DataStore에 함께 저장·복원하고 교체·삭제도 저장에 반영합니다. Google ID 토큰과 nonce는 저장하지 않습니다.
- 이유: 앱 프로세스 재시작 후 최신 토큰 쌍을 복원하고 일회용 refresh token의 회전 결과가 메모리와 디스크에서 일치하도록 합니다.
- 근거: 사용자 명시 요청 “DataStore쓰자” 및 자동 검증 요청, [이슈 #83](https://github.com/YAPP-admin/yapp-plus-android/issues/83).
- 반영: [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), `:core:datastore`, Data 로컬 DataSource, Network 저장 계약, `README.md`, `AGENTS.md`.
- 적용 상태: DataStore 1.2.1·Keystore AES-256-GCM·백업 제외 경로와 namespace별 파일 분리를 구현합니다. 저장·복원 기반 및 재시작 검증을 먼저 반영하며, App DI와 401 갱신·재시도 연결은 후속입니다.
- 대체한 결정: 없음.

## DEC-0025 · 저장된 YAPP 세션과 401 갱신 연결

- 기록일: 2026-10-20
- 결정: App은 DataStore 기반 토큰 저장소와 Network 세션 조율자를 singleton으로 연결합니다. Data는 인증되지 않은 HTTP client로 `POST /api/auth/session-renewals`를 호출해 회전된 access/refresh token 쌍을 반환합니다. 인증된 API의 401은 갱신 결과를 동시 요청과 공유하고 원래 요청을 한 번 재시도합니다.
- 이유: 저장·복원된 세션이 실제 API 요청의 만료 처리와 연결돼야 프로세스 재시작 뒤에도 회전형 refresh token을 사용할 수 있습니다.
- 근거: 사용자 요청과 [이슈 #76](https://github.com/YAPP-admin/yapp-plus-android/issues/76), DataStore 도입 [이슈 #83](https://github.com/YAPP-admin/yapp-plus-android/issues/83), 서버 OpenAPI의 갱신 계약.
- 반영: `:data`, `:core:network`, `:app`, [제품 정책](product-policy.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), `README.md`, `AGENTS.md`.
- 적용 상태: DataStore 토큰 복원, 갱신 API 호출, App singleton 조립과 401 재시도를 연결했습니다. 초기 Google ID 토큰 검증·YAPP 세션 발급은 후속입니다.
- 대체한 결정: DEC-0019의 Data API·App 조립 후속 부분과 DEC-0024의 App DI·401 연결 후속 부분만 완료합니다. 기존 갱신·동시 요청 공유 정책과 토큰 저장 정책은 유지합니다.

## DEC-0030 · 단일 값을 value class로 표현

- 기록일: 2026-10-21
- 결정: 이름·전화번호처럼 하나의 값을 별도 타입으로 표현할 때 Kotlin `@JvmInline value class`를 사용합니다. 값 자체에 속하는 규칙도 해당 타입이 소유합니다. 여러 값이나 상태를 함께 표현하는 객체에는 일반 `class` 또는 `data class`를 사용하며 모든 원시 값에 래퍼를 일괄 적용하지 않습니다.
- 이유: 하나의 의미를 나타내는 타입이 자체 값을 소유하도록 하여 호출부에서 서로 다른 원시 값을 혼용하는 일을 줄이고 단일 책임을 유지합니다.
- 근거: 사용자 명시 요청 및 [이슈 #102](https://github.com/YAPP-admin/yapp-plus-android/issues/102).
- 반영: [코딩 컨벤션](coding-conventions.md), `:domain:signup`의 `Name`·`PhoneNumber`.
- 적용 상태: `Name`과 `PhoneNumber`를 각각 `@JvmInline value class`로 구현하고, 비어 있지 않은 값인지 판단하는 규칙을 각 타입에 둡니다.
- 대체한 결정: 없음.

## DEC-0031 · Signup Feature가 가입 흐름 전체를 소유

- 기록일: 2026-10-21
- 결정: 이름 입력·휴대폰 입력·임시 가입 완료는 `:feature:signup`의 단일 `SignUpRoute`와 ViewModel이 한 화면 흐름으로 관리합니다. 현재 단계와 입력값은 Feature가 소유하고, App Navigation은 가입 목적지 하나와 완료 콜백만 연결합니다. AppNavHost는 Signup ViewModel을 가져오거나 Feature Route에 전달하지 않습니다.
- 이유: 연속된 가입 단계의 상태·검증·내부 이동 책임을 하나의 Feature에 두고, App이 화면별 ViewModel 조립에 관여하지 않도록 합니다.
- 근거: 사용자의 명시 요청 및 [이슈 #102](https://github.com/YAPP-admin/yapp-plus-android/issues/102).
- 반영: [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), `README.md`, `:feature:signup`, App Navigation.
- 적용 상태: `SignUpRoute`가 Feature 내부에서 `SignUpViewModel`을 가져오며 Name → Phone → Complete 전환과 단계 내부 뒤로 가기를 처리합니다. `AppNavigationDestination.SignUp` 하나를 등록하고 ViewModel 주입을 제거했습니다. `SavedStateHandle`은 입력값과 현재 단계를 보존합니다.
- 대체한 결정: App Navigation이 Signup ViewModel을 전달하던 구현 방식을 대체합니다. 개발 버전의 실제 서버 제출·완료 연결 상태는 DEC-0037을 따릅니다.

## DEC-0033 · 설정 화면과 계정 탈퇴 안내

- 기록일: 2026-10-21
- 결정: 설정 화면은 `:feature:setting`에 두고 My 탭에서 진입하며 이동 상태는 App이 소유합니다. 알림 토글, 약관 링크, 앱 버전, 로그아웃 확인을 제공합니다. 알림 토글은 현재 화면 상태로만 유지하며 푸시 전달·설정 저장 연동은 후속입니다. 앱 계정 삭제는 YAPP 동아리 탈퇴와 별개로 안내하고, 서버 탈퇴 API가 없으면 삭제 요청을 보내지 않습니다.
- 이유: 확인된 설정 화면을 제공하면서 미구현 계정 삭제·푸시 연동을 완료된 기능처럼 오인하게 하지 않기 위해서입니다.
- 근거: 사용자 요청과 [이슈 #104](https://github.com/YAPP-admin/yapp-plus-android/issues/104).
- 적용 상태: 설정 UI와 App 이동을 유지합니다. 개발 버전의 인증 실행은 DEC-0037에 따라 연결 전까지 비활성화하고 알림 토글은 숨깁니다.
- 반영: [제품 정책](product-policy.md), [아키텍처](architecture.md), [모듈 구조](module-structure.md), `README.md`, `AGENTS.md`, `:feature:setting`, App, My Feature.
- 대체한 결정: 없음.

## DEC-0036 · 비공개 테스트와 실제 개발 브랜치 분리

- 기록일: 2026-10-25
- 결정: `release/v1.0.1`은 임시 비공개 테스트 버전으로 보존하고 실제 서비스 개발은 `develop`에서 진행합니다. 같은 앱 ID `com.yapp.plus`와 Play 비공개 테스트 트랙을 사용합니다. 임시 브랜치를 개발 브랜치에 통째로 병합하지 않으며 필요한 수정만 선별해 반영합니다.
- 이유: 배포 중인 테스트 버전을 유지하면서 실제 서비스의 서버 계약에 맞는 개발을 이어가기 위해서입니다.
- 근거: 현재 세션의 사용자 확정 요청과 [이슈 #125](https://github.com/YAPP-admin/yapp-plus-android/issues/125).
- 반영: [제품 정책](product-policy.md), [CONTRIBUTING.md](../../CONTRIBUTING.md), `README.md`, `AGENTS.md`.
- 적용 상태: `release/v1.0.1` 작업 폴더와 브랜치를 보존합니다. 개발 전환은 최신 `develop` 기반의 순차 PR로 진행합니다.
- 대체한 결정: 일반 릴리즈의 develop 재병합 절차에서 임시 `release/v1.0.1`은 제외합니다.

## DEC-0037 · 개발 버전을 YAPP 서버 계약으로 정리

- 기록일: 2026-10-25
- 결정: 임시 테스트 백엔드의 구현·전용 계약·SQL·테스트·운영 문서를 개발 브랜치에서 제거합니다. 일정·출석 인증·보고는 기존 YAPP 어댑터로 연결하고 Google 크레덴셜 획득 코드는 유지합니다. 서버 인증 교환·가짜 가입 완료·프로필 저장은 연결하지 않으며 계약 없는 조회는 준비 중으로 표시합니다.
- 이유: 실제 서버에 없는 API나 로그인 성공을 만들지 않고 서버 구현에 맞춰 기능을 이어가기 위해서입니다.
- 근거: 사용자의 전체 제거 요청과 Google 크레덴셜만 유지하라는 후속 확정, [이슈 #125](https://github.com/YAPP-admin/yapp-plus-android/issues/125), 백엔드 dev 코드와 OpenAPI.
- 반영: App·Domain·Data·Login·Signup·Home·Board·My·Setting, [아키텍처](architecture.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), [제품 정책](product-policy.md), [회원·역할 정책](membership-policy.md).
- 적용 상태: YAPP 서버 어댑터와 토큰 저장·갱신 기반을 유지하고 임시 테스트 전용 파일과 관련 결정 기록을 개발 문서에서 정리했습니다. 해당 테스트 버전의 소스·문서·배포 데이터는 임시 릴리즈에 보존합니다. 실제 Google 인증·가입·회원 조회·공지·삭제 연결은 서버 계약 확인 후 구현합니다.
- 대체한 결정: 이전 임시 테스트의 인증·프로필·공지·출석 이력·삭제 연결을 개발 버전에서 종료합니다. 일반 계층 경계와 출석 정책은 유지합니다.

## DEC-0038 · 개발 화면을 원본 Figma에 맞춤

- 기록일: 2026-10-25
- 결정: `develop`의 앱 화면을 사용자가 지정한 Figma 앱 화면 페이지와 비교해 수정합니다. 실제 프레임의 `scale/t*` 타이포그래피, 배치·간격·색상·그래픽을 적용하며 Android 시스템 영역과 승인된 앱 데이터·기능 범위는 유지합니다.
- 이유: 개발 중인 앱의 원본 디자인 불일치를 해결하고, 미연동 기능을 화면 정합성을 위해 임의로 활성화하지 않기 위해서입니다.
- 근거: 현재 세션의 사용자 요청과 [이슈 #126](https://github.com/YAPP-admin/yapp-plus-android/issues/126).
- 반영: [디자인시스템](design-system.md), [제품 정책](product-policy.md), Core 디자인시스템·Login·SignUp·Home·Board·Schedule·My·Setting·App.
- 적용 상태: `develop`에서 PR #127·#128을 병합했고 후속 UI 정리 PR도 반영했습니다. 서버 계약이 없는 회원 정보·점수·공지 기능은 연결 전까지 준비 중으로 유지하며 실제 Google 인증·출석 서버 E2E는 이번 화면 비교 범위에 포함하지 않습니다.
- 대체한 결정: 없음. 비공개 테스트와 실제 서비스 개발의 브랜치 분리 결정은 DEC-0036을 따르고, 서버 연동 범위는 DEC-0037을 따릅니다.

## DEC-0039 · Google 네이티브 인증과 가입을 YAPP 서버에 연결

- 기록일: 2026-10-26
- 결정: Google Credential Manager의 Web Client ID와 매 로그인별 SHA-256 nonce를 사용해 ID Token·원본 nonce를 `POST /api/auth/oidc/google/native-authentications`로 검증합니다. `SIGNED_IN`의 YAPP token 쌍을 암호화 저장하고, `SIGNUP_REQUIRED`는 일회용 registration token을 메모리에만 유지해 `POST /api/users`의 이름·E.164 전화번호 제출로 이어갑니다. 앱 시작 시 저장된 세션을 복원해 인증 목적지를 정합니다.
- 이유: 개발 버전의 Google 로그인과 기존 가입 UI를 실제 서버 계약에 연결하고, 인증 정보의 수명과 세션 복원 경계를 분명히 하기 위함입니다.
- 근거: 사용자 제공 API 계약, [백엔드 APIDOC의 Google Native OIDC 인증 및 가입 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/dev/APIDOC.md), [Android Credential Manager 공식 구현 안내](https://developer.android.com/identity/sign-in/credential-manager-siwg-implementation).
- 반영: `:domain`의 인증 계약과 전화번호 변환, `:data`의 인증·가입 DataSource와 Repository, App Hilt·세션 복원·내비게이션, Login·Signup Feature, `AGENTS.md`, `README.md`, [아키텍처](architecture.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), [제품 정책](product-policy.md).
- 적용 상태: dev API와 통신하도록 로컬 `local.properties`에 제공된 개발 API host를 지정했습니다. 2026-10-27 실기기에서 실제 Google 로그인, 가입, 암호화 세션 저장과 프로세스 재시작 후 메인 복원을 확인했습니다. 인증 후 Navigation의 메인 스레드 위반을 수정했고 앱 빌드·lint, 관련 모듈 단위 테스트 43건을 통과했습니다.
- 추가 확인: 같은 날 사용자 요청으로 dev 계정을 `DELETE /api/users/me`로 탈퇴시켜 성공 `204`, 기존 세션 거절 `401`, 로컬 token 삭제와 로그인 복귀를 확인했습니다. 서버는 계정·동아리 통합 탈퇴를 제공합니다. 연결 상태는 [제품 정책](product-policy.md#계정-기수-통합-탈퇴)을 따릅니다.
- 대체한 결정: DEC-0037의 Google 인증 교환·가입 연결 후속 상태를 완료합니다. 공지·회원 프로필·출석 조회 미연동 범위는 유지합니다. 로그아웃·통합 탈퇴 적용 상태는 이후 DEC-0041에서 정합니다.

## DEC-0040 · 가입 필드 오류와 이름 수정 시 전화번호 유지

- 기록일: 2026-10-26
- 결정: 가입은 이름·전화번호를 함께 제출하고 서버의 `fieldErrors`를 입력별로 표시합니다. 이름만 오류이면 이름 입력으로 돌아가고, 수정 후 기존 전화번호로 바로 재제출합니다. 두 필드 모두 오류이면 이름을 먼저 고친 뒤 전화번호도 수정합니다. 전화번호 중복은 전화번호 입력 오류로 처리합니다.
- 이유: 오류가 난 입력으로 돌아가 수정하게 하면서 오류가 없는 전화번호의 재입력을 생략하기 위함입니다.
- 근거: 사용자의 명시적 구현 요청, dev 서버의 입력 오류 응답과 [백엔드 가입 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/dev/APIDOC.md#8-user-가입-완료).
- 반영: `RegistrationFieldError`, 가입 오류 DTO·Repository 매핑, Signup ViewModel·Route·입력창·Preview·테스트, App 인증 후 화면 이동과 opt-in 실서버 가입 E2E, [제품 정책](product-policy.md), [설계 패턴](design-patterns.md).
- 적용 상태: 이름 화면 복귀·전화번호 유지·이름 화면 재제출·수정 상태 복원·동시 필드 오류·전화번호 중복 처리를 로컬 작업 트리에 구현했습니다. 관련 단위 테스트 22건, 변경 모듈 ktlint, App debug 빌드·lint를 통과했습니다. 2026-10-27 Samsung 실기기와 실제 dev 서버에서 Google 신규 인증 → 이름 101자 제출 `400` → 이름 화면 오류 표시 → 이름만 수정 → 동일한 E.164 전화번호로 재제출 `200` → 가입 완료·메인 진입·Activity 재생성을 E2E로 통과했고 프로세스 종료·재시작 후에도 메인 세션 복원을 확인했습니다. 번호 중복 `409`의 입력창 표시도 별도로 확인했습니다. 가입 토큰은 메모리 전용으로 유지하며 만료 시 기존 로그인 복귀를 따릅니다.
- 대체한 결정: DEC-0039의 일반적인 가입 실패 처리에 필드별 복구 동작을 구체화합니다.

## DEC-0041 · 로그아웃과 계정·기수 통합 탈퇴 연결

- 기록일: 2026-10-27
- 결정: 로그아웃은 `DELETE /api/auth/sessions/current`에 현재 refresh token을 전달하고 서버 성공 뒤 로컬 token 쌍을 삭제해 로그인 화면으로 이동합니다. 실패하면 현재 세션을 보존하고 재시도할 수 있게 합니다. `DELETE /api/users/me`는 앱 계정만 삭제하는 API가 아니라 모든 기수 소속과 세션을 종료하고 개인정보를 비식별화하는 통합 탈퇴입니다. 사용자가 확인한 뒤 Bearer access token으로 요청하고, 서버 성공 뒤에만 로컬 token을 삭제해 로그인 화면으로 이동합니다.
- 이유: 기존 설정의 앱 계정 삭제 안내를 실제 서버 계약과 일치시키고, 서버에서 처리되지 않은 탈퇴를 로컬 세션 삭제로 완료 처리하지 않기 위함입니다.
- 근거: 사용자의 확정 요청과 [이슈 #149](https://github.com/YAPP-admin/yapp-plus-android/issues/149), [백엔드 인증·로그아웃 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/dev/api/src/main/kotlin/backend/yappplus/auth/AuthController.kt), [백엔드 통합 탈퇴 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/dev/api/src/main/kotlin/backend/yappplus/auth/UserWithdrawalController.kt).
- 반영: [제품 정책](product-policy.md), [회원·역할 정책](membership-policy.md), [아키텍처](architecture.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), `AGENTS.md`, `README.md`, Domain·Data 인증 Repository와 설정 문구·App 인증 흐름.
- 적용 상태: 빌드 복구 [PR #154](https://github.com/YAPP-admin/yapp-plus-android/pull/154), Domain [PR #153](https://github.com/YAPP-admin/yapp-plus-android/pull/153), Data [PR #155](https://github.com/YAPP-admin/yapp-plus-android/pull/155), 설정 안내 [PR #156](https://github.com/YAPP-admin/yapp-plus-android/pull/156)을 순서대로 반영했습니다. App 실행 콜백·성공 후 로그인 복귀·오류 재시도·중복 방지와 취소 후 로딩 해제를 연결했습니다. App 단위 테스트 8건, API 35 에뮬레이터 UI 테스트 6건, App debug 빌드·lint·KtLint를 통과했습니다. 실제 Settings 서버 E2E는 확인하지 못했습니다.
- 대체한 결정: DEC-0033의 앱 계정 삭제와 YAPP 동아리 탈퇴를 별도 처리한다는 정책을 대체합니다. 설정 화면 구조와 다른 알림·약관 정책은 유지합니다.

## DEC-0042 · 앱 기능 명세서 ver 2를 화면 요구 기준으로 반영

- 기록일: 2026-10-26
- 결정: 사용자가 제공한 [기능 명세서 `🟠APP (ver 2)` 탭](https://docs.google.com/spreadsheets/d/1K-2VwMPkoT8kq-bujUIMetTPKal11Iql2GUmMd1M7eE/edit?gid=848576421#gid=848576421)을 화면의 목표 요구로 기록합니다. Android에는 Google 로그인을 적용하며 시트의 iOS 전용 Apple 로그인 요구를 Android로 확대하지 않습니다.
- 이유: 갱신된 기능 명세와 후속 화면 요구를 현재 제품 정책에서 추적하기 위해서입니다.
- 근거: 사용자의 2026-10-26 Wiki 갱신 요청과 시트의 변경 표시. Drive 개정 이력은 제공되지 않아 변경 열과 기존 APP 탭을 대조했습니다.
- 반영: [제품 정책](product-policy.md)의 `앱 기능 명세서 ver 2` 절.
- 적용 상태: 문서에 목표와 현재 구현을 구분해 기록했습니다. DEC-0039의 YAPP 서버 인증·가입 결정은 현재 구현 기준으로 유지합니다. 멤버십 연결·승인, 팀 사진 출석·검수, 공식 회원·출석 조회, 공지·알림 서버 연결은 후속입니다. 탈퇴 보존 문구의 범위와 기간은 미확정으로 유지합니다.
- 대체한 결정: 없음.

## DEC-0043 · My 출석 조회 연결과 Figma UI 기준

- 기록일: 2026-10-29
- 결정: My의 출석 점수·이력은 확인된 사용자 API에 연결하고, 화면·별도 이력 이동·설정·확인 팝업은 Figma를 따릅니다. Figma에 없는 앱 버전·탈퇴 문의 이메일·확장 안내를 제거합니다. 서버와 탈퇴 문구의 차이를 설명한 뒤 사용자가 Figma 원문을 명시적으로 재확인했습니다. 서버의 통합 탈퇴·비식별화·이력 보존·실패 시 세션 보존은 변경하지 않습니다. 미지원 API에 대해서는 사용자가 “api 연결만 비워놔”라고 명시했으므로 Figma UI를 유지하고 조회값을 `—`로 표시하며 API·문의 URL 연결만 비워 둡니다. 알림 토글은 화면 상태만 변경합니다.
- 이유: 준비 중 화면에서 제공되는 실제 출석 조회로 전환하고, 요청된 UI/UX 기준과 구현을 맞추기 위해서입니다.
- 근거: 사용자의 마이페이지 구현 요청·탈퇴 문구 후속 답변, [Figma](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=222-40302), [이슈 #160](https://github.com/YAPP-admin/yapp-plus-android/issues/160), [서버 API 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/dev/APIDOC.md).
- 반영: [제품 정책](product-policy.md), [아키텍처](architecture.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), [회원·역할 정책](membership-policy.md), [디자인시스템](design-system-components.md), AGENTS·README, Domain·Data·My·Setting·App.
- 적용 상태: 로컬 구현과 빌드·lint·ktlint·단위 58건·API 35 UI 13건 검증을 완료했습니다. 사용자용 프로필·지각 면제권·알림 설정·문의 URL과 진행률 산식은 미확인입니다. 해당 UI는 유지하고 API 연결과 산식만 비워 두었습니다. 서버 응답을 임의 계산이나 예시 값으로 대체하지 않습니다. Domain 계약·Data·공통/설정 UI와 My 요약 컴포넌트는 PR #162~#171로 병합했습니다. 2026-10-30 추가 적용: PR #174·#175·#178로 My 프로필·메인·별도 내역 UI와 조회·이동을 반영했습니다.
- 대체한 결정: DEC-0033/DEC-0038의 앱 버전·추가 탈퇴 안내 유지, DEC-0041의 화면 안내 문구, DEC-0042의 My 출석 조회 미연결 상태를 갱신하며 My·설정의 미지원 필드 숨김을 이 작업에 한해 UI 유지로 대체합니다. 통합 탈퇴 서버 계약과 다른 기능 범위는 유지합니다.


## DEC-0044 · 서버에 존재하는 사용자 API의 후속 연결

- 날짜: 2026-10-30
- 결정: 사용자의 “api 연동 마저해줘”, “서버에있는것만진행해”에 따라 확인된 서버 계약을 연결합니다. 로그인·가입 응답의 이름을 세션과 함께 저장해 My에 표시하고 공지 목록·상세·읽음, 홈 주간 공지와 사전 보고를 연결합니다. 지각 보고는 현재 사용자 Controller가 허용하는 `LATE`를 사용하며 추가 시간 선택은 제거합니다.
- 근거: [이슈 #161](https://github.com/YAPP-admin/yapp-plus-android/issues/161), [백엔드 f1d29f4](https://github.com/YAPP-admin/yapp-plus-backend/tree/f1d29f44bfbf10793baabce7c0e2f2efdf3a307c), Figma 공지 상세·보고 시트.
- 적용 상태: API·화면 연결, 전체 모듈 debug 빌드·lint, ktlint, 관련 단위 테스트 97건(Data 52·Network 23·Board 4·Home 9·My 9), API 35 UI 테스트 18건과 Wiki 검사를 완료했습니다. 검증은 Fake·MockEngine·에뮬레이터를 사용했으며 실제 서버 계정으로 E2E 요청을 수행하지 않았습니다. 팀 사진 presign·업로드·제출과 푸시 기기 등록·해제는 Domain·Data·App 조립까지 구현하며, 본인 membershipId·팀원 목록과 Android Firebase 설정을 확인하기 전에는 UI 제출·실제 푸시 수신을 완료로 간주하지 않습니다. 이름이 없던 기존 저장 세션은 다음 로그인 성공부터 이름을 표시합니다. 미확인 회원 정보·면제권·알림 설정·문의 연결은 비워 둡니다.
- 추가 적용: PR #172~#178로 가입 Route 정리, App 조립, My·홈·게시판의 화면과 API 연결을 영역별로 나누어 병합했습니다. 최종 통합 코드에서 전체 단위 테스트 129건, 전체 debug 빌드·lint·ktlint와 테스트 APK 빌드를 통과했습니다. 동일 기능 코드의 API 35 UI 테스트 18건도 다시 통과했습니다.
- 대체한 결정: DEC-0038·DEC-0042·DEC-0043의 공지·보고·이름 미연결 상태를 갱신합니다. Figma UI·문구 기준과 확인되지 않은 값의 빈 표시 정책은 유지합니다. 서버 조회 계약이 없는 관리자 전용 API를 사용자 토큰으로 대체 호출하지 않습니다.

## DEC-0045 · Data 모델의 매핑을 원본 타입에 캡슐화

- 기록일: 2026-10-30
- 결정: 직접 소유하는 DTO·Data 모델의 매핑은 확장 함수 대신 원본 타입 내부의 `toData()`·`toDomain()` 멤버 함수로 둡니다. DataSource·Repository는 해당 함수를 호출해 변환 결과를 사용합니다.
- 이유: 변환 책임을 원본 모델에 모으고 요청·저장소 코드와 분리하기 위함입니다.
- 근거: 현재 세션에서 `AttendanceScoreResponse.toData()`와 같은 매핑을 수신 타입 내부로 캡슐화하라는 사용자 요청.
- 반영: [설계 패턴](design-patterns.md), 출석 점수·이력 DTO와 Data 모델. 같은 작업의 공지 매핑에도 적용합니다.
- 적용 상태: 출석 점수·이력과 공지 매핑에 반영했으며 기존 검증·변환 동작을 유지합니다.
- 대체한 결정: 없음. Data의 DTO·Domain 변환 소유권과 계층 의존성은 유지합니다.
