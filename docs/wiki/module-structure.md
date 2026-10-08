---
authority: canonical
status: active
last_verified: 2026-10-08
---

# 모듈 구조

실제 등록 여부는 [settings.gradle.kts](../../settings.gradle.kts)에서 확인합니다. 계층 경계는 [아키텍처](architecture.md)를 따릅니다.

| 모듈 | 패키지 기준 | 현재 상태 또는 추가 시점 |
| --- | --- | --- |
| `:app` | `com.yapp.plus` | 앱 진입점·Navigation 2 가입 UI 데모 흐름 구현됨; 메인 목적지·debug 전용 홈 시작 경로 후속 |
| `:core:designsystem` | `com.yapp.plus.core.designsystem` | 테마·공통 컴포넌트·인증 공통 표현 구현됨 |
| `:core:preview` | `com.yapp.plus.core.preview` | debug 전용 범용 Preview provider·입력 fixture 구현됨 |
| `:feature:attendance` | `com.yapp.plus.feature.attendance` | 홈·일정·게시판·My 데모 화면과 공통 하단 탭 조합 구현됨; App 진입 연결 후속 |
| `:feature:login` | `com.yapp.plus.feature.login` | 카카오 로그인·오류 UI 구현됨; 앱 초기 화면 |
| `:feature:signup` | `com.yapp.plus.feature.signup` | 이름·휴대폰 입력·가입 상태와 승인 대기 둘러보기 버튼 UI 구현됨 |
| `:domain` | `com.yapp.plus.domain` | 순수 Kotlin/JVM 기본 설정됨 |
| `:data` | `com.yapp.plus.data` | Android Library 기본 설정됨 |
| `:core:network` | `com.yapp.plus.core.network` | Android Library 기본 설정됨 |
| `build-logic` | Gradle convention plugins | 별도 included build; 앱 모듈이 아님 |
| `:core:datastore` | `com.yapp.plus.core.datastore` | 로컬 저장 필요 시 추가 |

9개 Gradle 모듈의 공통 설정은 `build-logic` convention plugin으로 관리합니다. App은 Hilt 진입점을 갖고, `:core:network`는 Ktor Android client와 Kotlin serialization JSON 의존성을 사용합니다. 실제 API 호출과 DTO는 서버 계약이 정해진 뒤 구현합니다. Domain과 Data의 기능별 분리는 규모와 의존 관계에 따라 결정합니다.

`:feature:login`과 `:feature:signup`은 서로 의존하지 않고 `:core:designsystem`의 공통 인증 표현을 재사용합니다. App은 Navigation Compose 2.10.2의 타입 기반 목적지로 카카오 버튼 → 이름 → 휴대폰 번호 → 운영진 승인 대기의 UI 데모 경로를 연결합니다. App의 공통 ViewModel과 `SavedStateHandle`이 데모 입력값을 유지·복원하며 Feature에는 표시값과 콜백만 전달합니다. 전체 뒤로 가기와 시스템 inset 적용·소비도 App이 소유합니다. 회원가입 진입 안내·버튼·구분선은 제공하지 않습니다. 입력 검증·실제 인증·가입 서버 처리는 포함하지 않습니다.

`:feature:attendance`의 메인 UI는 홈·일정·게시판·My의 정적 데모 화면을 `YappBottomNavigation`과 `YappBottomNavigationItem`으로 조합합니다. Feature가 탭 선택을 유지·복원하고, 시스템 inset과 전체 화면 이동은 App이 소유합니다. `:feature:signup`은 승인 대기 상태에서만 “앱 둘러보기” 버튼으로 전달받은 콜백을 호출합니다. App의 타입 기반 메인 목적지 연결과 실제 기기 캡처용 임시 debug 홈 시작 경로는 후속 `feat` PR에서 구현합니다. release의 기존 로그인 진입 흐름은 유지합니다. 데모 표시는 실제 출석 기록이나 서버 확정 상태를 의미하지 않습니다.

## 파일 배치

- Android 모듈의 Kotlin 소스는 `src/main/java/<패키지 경로>/`에 둡니다. 순수 Kotlin/JVM Domain 소스는 `domain/src/main/kotlin/<패키지 경로>/`에 둡니다.
- 단위 테스트는 `src/test/`, 기기 테스트는 `src/androidTest/`의 해당 패키지에 둡니다.
- App에는 Android 진입점·전체 내비게이션·객체 조립을, Feature에는 화면·ViewModel·UiState와 기능별 UI 변환을 둡니다.
- Domain에는 모델·정책·Repository 계약·필요한 UseCase를, Data에는 Repository 구현·DataSource·DTO·Domain 변환을 둡니다.
- Designsystem의 테마는 `core/designsystem/src/main/java/com/yapp/plus/core/designsystem/theme/`에 있습니다. 공통 컴포넌트도 이 모듈에서 관리합니다.
- 인증의 공통 표현은 Designsystem의 `component/auth/`에 둡니다. 표시값과 콜백만 받고 Feature 상태·목적지·인증 정책을 알지 못합니다. 기능별 문구·카카오 로고·상태 변환은 Feature가 소유합니다.
- Preview 전용 소스·리소스는 각 모듈의 `src/debug/`에 둡니다. `:core:preview`도 debug 소스에만 provider·fixture를 제공하며 Designsystem·Feature·Domain·Data·Navigation에 의존하지 않습니다. 소비 모듈은 필요한 경우에만 `debugImplementation(project(":core:preview"))`으로 연결합니다. `src/main`은 debug 타입을 참조하지 않고 앱 release 의존성 그래프에 Preview 모듈을 포함하지 않습니다.
- 사용자 문구는 해당 Android 모듈의 `src/main/res/values/strings.xml`에 둡니다.
- 범용 파일·모듈을 미리 만들지 않습니다. 여러 사용처의 실제 공통 책임이 확인되면 공통화합니다.

## 모듈 변경 절차

모듈을 추가하거나 옮길 때 실제 책임·사용처·계약·호출부를 확인합니다. 파일·패키지를 배치하고 `settings.gradle.kts`에 등록한 뒤 [아키텍처의 의존성 방향](architecture.md)과 App의 객체 조립을 확인합니다. 재사용할 Fake·테스트 도구는 필요할 때 테스트 의존성으로만 연결합니다. 영향받는 모듈과 호출부를 검증하고 이 문서, `AGENTS.md`, `README.md`의 상태를 함께 갱신합니다. 없는 모듈이나 검사 도구의 명령을 실행했다고 보고하지 않습니다.
