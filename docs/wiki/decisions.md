Warning: truncated output (original token count: 10648)
Total output lines: 314

---
authority: canonical
status: active
last_verified: 2026-10-21
---

# 결정 기록

확정된 정책의 변경 이유를 날짜순으로 남깁니다. 현재 적용 규칙은 분야별 문서에서 읽습니다. 새 결정에는 다음 `DEC-0032`부터 순서대로 ID를 부여하고, 이전 결정을 대체하면 해당 ID를 기록합니다. 이전 항목은 삭제하거나 현재 규칙처럼 수정하지 않습니다.

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
- 적용 상태: 모듈 분리 PR #29 병합 후 [이슈 #35](https://github.com/YAPP-admin/yapp-plus-android/issues/35)에서 Navigation Compose 2.10.2의 타입 기반 목적지와 App의 공통 ViewModel·`SavedStateHandle`로 데모 흐름을 구현했습니다. 뒤로 가기·반복 탭·입력값 유지·복원을 연결하고 입력 검증 없이 다음 버튼을 활성화합니다. 후속 [DEC-0028](#dec-0028--신규-google-인증-뒤-임시-가입-완료-흐름)에서 신규 Google 인증 뒤 완료 화면으로 이동하도록 바꿨으며 실제 YAPP 가입 서버 처리는 계속 미연동입니다.
- 대체한 결정: 신규 Google 인증의 다음 화면에 관한 기존 데모 상태를 DEC-0028로 대체합니다.

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
- 대체한 결정: DEC-0012의 승인 대기에서 메인 탭으로 진입하는 결정. 정적 탭 데모와 debug 홈 시작 경로는 유지합니다. 가입 흐름의 승인 대기 종료점은 DEC-0028에서 대체합니다.

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
- 적용 상태: 게시판 화면과 분류 상태를 `:feature:main:board`에, My 화면과 컴포넌트를 `:feature:main:my`에, 일정 화면·상태·모델·리소스를 `:feature:main:schedule`에, 홈 화면·상태·모델·컴포넌트·리소스를 `:feature:main:home`에 분리했습니다. App이 네 화면과 탭 선택을 조립하고 기존 `:feature:attendance`는 제거했습니다. 공통 헤더·데모 안내·아이콘 라벨은 `YappMainHeader`, `YappDemoDataLabel`…648 tokens truncated… 명시 요청, [이슈 #88](https://github.com/YAPP-admin/yapp-plus-android/issues/88).
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

## DEC-0026 · MVP 데이터 공급자를 기능별 DataSource 주입으로 교체

- 기록일: 2026-10-20
- 결정: Domain Repository 계약 아래 Data의 `RepositoryImpl`이 기능별 DataSource 인터페이스를 생성자로 주입받습니다. 현재 Supabase, 향후 YAPP 원본 서버 어댑터를 App에서 선택하고 Fake는 같은 DataSource 계약의 테스트 대역으로 사용합니다. 공급자 교체 때문에 Domain·Feature를 수정하지 않습니다.
- 이유: Repository 내부의 구체 DataSource 생성과 SDK·HTTP 의존을 제거해 테스트 MVP와 이후 원본 서버가 같은 기능 계약을 구현하도록 합니다.
- 근거: 사용자의 명시 요청과 승인된 MVP 계획, [이슈 #90](https://github.com/YAPP-admin/yapp-plus-android/issues/90).
- 반영: [아키텍처](architecture.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), 인증·공지 Domain 계약.
- 적용 상태: `AuthRepository`와 credential·사용자 식별 세션 모델, `NoticeRepository`와 공지 모델·유형을 추가했습니다. 기존 일정·출석 Domain 계약을 재사용하고 DataSource 인터페이스 주입·공통 세션 모델을 구현했습니다. Supabase Auth DataSource·Repository와 namespace 분리 암호화 세션 저장, 일정·공지·출석 검증 Supabase DataSource와 공지 Repository를 App Hilt에 연결했습니다. YAPP 출석·보고 HTTP 어댑터는 보존하며 Supabase 보고 호출은 backend가 없어 일반 실패로 끝납니다. Login Feature의 Domain credential 전달과 App의 Supabase 교환·시작 시 세션 복원·오류 재시도를 연결했습니다. 홈의 주간 공지와 게시판의 유형별 목록·상세를 Repository에 연결했습니다. Supabase MVP의 홈에서 보고 진입·시트·완료 안내와 보고 전용 상태를 제거했으며 Domain 보고 계약과 Data/YAPP 어댑터는 보존합니다. YAPP 초기 로그인·회원 식별자 대응 같은 미확정 계약은 정의하지 않습니다.
- 대체한 결정: 없음. 기존 계층 경계 안에서 DataSource 주입 방식을 명시합니다.

## DEC-0027 · Supabase 인증과 테스트 데이터로 비공개 MVP 구성

- 기록일: 2026-10-20
- 결정: Google Credential Manager의 ID 토큰·원본 nonce를 Supabase Auth에 교환하고 세션 복원·로그아웃을 제공합니다. 세션·공지 테스트 데이터를 조회하며 새 출석은 테스트 세션별로 직접 허용한 인증 사용자와 4자리 코드에 한정합니다. 시작 20분 전부터 종료 시각까지 DB가 판정하고 중복 제출은 원래 기록을 반환하며 본인만 재조회합니다. 테스트 종료 뒤 자동 삭제 없이 수동으로 운영 데이터로 전환합니다. Supabase Google provider는 사용자가 연 Dashboard에서 컴퓨터 사용으로 설정합니다.
- 이유: YAPP 초기 로그인과 공지 조회 계약이 준비되기 전에 비공개 테스트의 핵심 기능을 실제 인증·데이터로 확인합니다.
- 근거: 사용자 확정 및 승인된 MVP 계획, [이슈 #90](https://github.com/YAPP-admin/yapp-plus-android/issues/90).
- 반영: [제품 정책](product-policy.md), [아키텍처](architecture.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md).
- 적용 상태: Domain 계약·DataSource 주입, [DB migration](../../supabase/migrations/20261020093715_mvp_test_sessions_notices_attendance.sql)의 2주 더미 데이터·RLS·본인 기록 조회·서버 검증 RPC, Supabase Auth DataSource·Repository와 분리 암호화 세션 저장을 구현했습니다. 기수 정보·코드·허용 목록은 비워 둡니다. 로컬 PostgreSQL에서 스키마 계약을 검증했고 Auth 어댑터는 MockEngine 테스트를 통과했으며 실제 프로젝트에는 migration을 적용하지 않았습니다. 일정·공지·출석 검증 어댑터와 인증 REST helper를 연결해 Supabase 세션의 401 복원·갱신 후 원래 GET·POST를 한 번 재시도합니다. 출석 검증은 네 자리 ASCII 코드와 세션만 보내고 서버의 단일 확인 행·처리 시각을 검증하며 migration에 보고 backend는 포함하지 않습니다. 일정은 서버 주간 범위·정렬을 사용하며 테스트 세션의 미설정 기수만 0으로 매핑합니다. Google 요청에는 nonce SHA-256 hex를 보내고 원본은 Domain credential로 교환합니다. App은 시작 시 세션 복원·실패 재시도를 제공하며 모든 빌드가 인증된 세션으로 메인에 진입합니다. 홈의 주간 공지와 게시판 목록·상세·유형 필터를 Repository에 연결했습니다. Supabase MVP의 홈에서 보고 진입·시트·완료 안내와 보고 전용 상태를 제거했으며 Domain 보고 계약과 Data/YAPP 어댑터는 보존합니다. 실제 테스트 계정·코드 설정과 운영 데이터 전환은 후속입니다. 기존 확정 YAPP 출석 정책을 임의로 변경하지 않습니다.
- 대체한 결정: DEC-0017의 ID 토큰 발급까지만 수행하는 범위를 MVP의 Supabase 교환으로 확장합니다. Google 단일 로그인 UI와 토큰 발급만으로 로그인 완료하지 않는 원칙은 유지합니다. 기존 debug 홈 시작 경로는 인증 세션 기반 시작으로 대체합니다. DEC-0024·DEC-0025의 YAPP 세션 저장·갱신 경로는 Supabase 세션과 구분해 유지합니다.

## DEC-0028 · 신규 Google 인증 뒤 임시 가입 완료 흐름

- 기록일: 2026-10-20
- 결정: 새 Google 인증을 Supabase 세션으로 교환한 뒤 이름 입력 → 휴대폰 번호 입력 → 가입 완료 화면을 표시합니다. 제출용 임시 흐름에서는 실제 운영진 인증·가입 API를 기다리지 않으며, 완료 액션으로 메인 화면에 진입합니다. 이름·휴대폰 번호는 서버에 전송하지 않습니다. 저장된 Supabase 세션 복원은 가입 단계를 건너뜁니다.
- 이유: 제출용 앱에서 회원 입력 과정을 보여 주면서 미확정된 YAPP 가입·운영진 인증 계약을 추측해 구현하지 않기 위해서입니다.
- 근거: 사용자 명시 요청 및 [이슈 #100](https://github.com/YAPP-admin/yapp-plus-android/issues/100).
- 반영: [제품 정책](product-policy.md), [설계 패턴](design-patterns.md), [모듈 구조](module-structure.md), `README.md`, App 인증·Navigation 조립.
- 적용 상태: App 인증 상태가 신규 Google 인증과 세션 복원을 구분해 시작 목적지를 선택하고, 기존 `:feature:signup` 입력·완료 화면을 조립합니다. 가입 완료 시 입력값을 지우고 인증 상태를 확정해 메인 그래프로 전환합니다. 실제 YAPP 회원 등록은 수행하지 않습니다.
- 대체한 결정: DEC-0011·DEC-0013의 가입 흐름이 승인 대기에서 끝난다는 동작을 신규 Google 인증의 임시 제출 흐름에 한해 대체합니다. 실제 가입 승인 정책은 미정으로 유지합니다.

## DEC-0029 · 가입 입력 필수화와 사용자 화면의 정적 데모 제거

- 기록일: 2026-10-21
- 결정: 가입 이름과 휴대폰 번호는 공백이 아닌 값이어야 다음 단계로 진행합니다. 전화번호 형식은 검증하지 않으며 입력값도 서버에 보내지 않습니다. 출시 화면에서 가짜 회원 프로필·출석 수치, 고정 일정 목록과 데모 안내를 제거합니다. 회원 정보 API가 연결되지 않은 My 화면은 준비 중 안내만 표시합니다. 비공개 테스트에 승인한 Supabase 세션·공지 데이터는 유지합니다.
- 이유: 빈 가입 정보와 정적 예시값이 실제 회원·출석·일정 정보처럼 보이는 것을 막고, 제출용 MVP의 실제 조회 흐름을 그대로 보여 주기 위해서입니다.
- 근거: 사용자 명시 요청, [이슈 #102](https://github.com/YAPP-admin/yapp-plus-android/issues/102).
- 반영: [제품 정책](product-policy.md), [회원·역할 정책](membership-policy.md), [모듈 구조](module-structure.md), [디자인시스템](design-system.md), `README.md`, `:domain:signup`, App Navigation, My·일정 화면.
- 적용 상태: Domain의 `Name`과 `PhoneNumber`가 각각 공백 여부를 판단하고, `:feature:signup`의 각 Route가 입력 상태를 조합해 다음 버튼 활성화와 이동 콜백을 제어합니다. App Navigation은 같은 `SignUpViewModel`을 가입 화면에 전달해 입력값을 유지하며 SavedStateHandle의 기존 키 값을 새 키로 옮깁니다. 일정 화면의 하드코딩 예시 경로와 My의 프로필·점수 샘플을 제거했습니다. My는 미연동 안내를 표시하고 세션·공지 테스트 데이터는 Supabase Repository를 통해 조회합니다.
- 대체한 결정: DEC-0012의 My·일정 정적 데이터 표시와 DEC-0020의 다른 화면 데모 UI를 유지하던 부분만 대체합니다. 화면별 debug Preview와 비공개 테스트용 Supabase 샘플 데이터는 유지합니다.

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
- 대체한 결정: DEC-0029의 App Navigation이 같은 Signup ViewModel을 가입 화면들에 전달한다는 구현 방식을 대체합니다. 이름·휴대폰 필수 조건과 서버에 입력값을 보내지 않는 제품 정책은 유지합니다.
