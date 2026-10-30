---
authority: canonical
status: active
last_verified: 2026-10-30
---

# 모듈 구조

실제 등록 여부는 [settings.gradle.kts](../../settings.gradle.kts)에서 확인합니다. 계층 경계는 [아키텍처](architecture.md)를 따릅니다.

| 모듈 | 패키지 기준 | 현재 상태 또는 추가 시점 |
| --- | --- | --- |
| `:app` | `com.yapp.plus` | 앱 진입점·세션 복원에 따른 시작 목적지·Google 인증 결과와 가입 이동·로그아웃/통합 탈퇴 상태와 내비게이션·YAPP Data 조립·토큰 저장/갱신·탭 내비게이션 |
| `:core:designsystem` | `com.yapp.plus.core.designsystem` | 테마·공통 컴포넌트·인증 공통 표현 구현됨 |
| `:core:preview` | `com.yapp.plus.core.preview` | debug 전용 범용 Preview provider·입력 fixture 구현됨 |
| `:feature:main:home` | `com.yapp.plus.feature.main.home` | YAPP 이번 주 세션·출석 인증·사전 보고·주간 공지 연결; 알림·점수 진입 숨김 |
| `:feature:main:schedule` | `com.yapp.plus.feature.main.schedule` | 현재 기수 일정 API Route·ViewModel·UI 매핑 및 일정 화면·모델 구현됨; App에서 연결 |
| `:feature:main:board` | `com.yapp.plus.feature.main.board` | 공지 분류·커서 목록·Markdown 상세·과제·첨부 이미지·읽음 확인 API 연결 |
| `:feature:main:my` | `com.yapp.plus.feature.main.my` | Figma 요약·규칙·별도 이력 UI, 출석 점수·커서 조회 ViewModel·오류 재시도; 인증 응답 이름 표시; 미확인 기수·역할·직군·면제권·문의 UI 유지 |
| `:feature:setting` | `com.yapp.plus.feature.setting` | 약관·알림 UI·Figma 로그아웃/회원 탈퇴 확인·진행·오류 재시도 UI; 앱 버전·확장 안내 제거; 인증 요청은 App 콜백 사용 |
| `:feature:login` | `com.yapp.plus.feature.login` | Google Credential Manager ID 토큰·원본 nonce 획득, nonce 해시·취소·중복 요청·인증 오류 재시도 |
| `:feature:signup` | `com.yapp.plus.feature.signup` | 가입 토큰을 받아 이름·휴대폰 입력·검증·서버 제출·완료 상태를 관리; 입력은 복원하고 토큰은 메모리 전용 |
| `:domain` | `com.yapp.plus.domain` | 순수 Kotlin/JVM; 인증·세션 Repository 계약과 인증/세션 작업 결과, 일정·출석 계약·가입 입력 값 객체; 사용자 출석 조회·이름·공지·사진·기기 Repository 계약과 Data 구현 연결 |
| `:data` | `com.yapp.plus.data` | Google OIDC 인증·회원가입·로그아웃·통합 탈퇴·세션 갱신 및 YAPP 일정·출석·공지·팀 사진·푸시 기기 어댑터와 Repository, 이름·토큰 암호화 저장 연결 |
| `:core:network` | `com.yapp.plus.core.network` | Ktor HTTP client·JSON·환경별 base URL·debug 로그·토큰 복원/교체 조율·401 갱신 및 중복 방지 구현; API DTO는 `:data` 소유 |
| `build-logic` | Gradle convention plugins | 별도 included build; 앱 모듈이 아님 |
| `:core:datastore` | `com.yapp.plus.core.datastore` | DataStore 세션 레코드·Keystore 암호화·백업 제외 파일 저장 구현됨; Data에서 사용 |

14개 Gradle 모듈의 공통 설정은 `build-logic` convention plugin으로 관리합니다. App은 Hilt 진입점과 네트워크·Repository 객체 조립을 갖고, `:core:network`는 Ktor Android client, JSON 직렬화, 환경별 base URL과 인증·세션 갱신 조율을 제공합니다. `:data`는 Network의 영구 저장 계약을 `:core:datastore`에 연결하며 App이 singleton으로 조립합니다. 저장된 최신 토큰 쌍은 첫 인증 요청 전에 복원합니다. 세션 일정 조회의 Domain 계약, `:data`의 DataSource·DTO 매핑·Repository 구현, 홈·일정 Feature의 ViewModel·UI 변환이 연결되어 있습니다. Domain과 Data의 기능별 분리는 규모와 의존 관계에 따라 결정합니다.

개발 버전은 YAPP 서버의 Google 네이티브 인증·회원가입·로그아웃·통합 탈퇴·일정·출석 인증·보고 어댑터를 App Hilt에 연결합니다. 서버에 확인된 계약만 DataSource·DTO로 구현합니다. My 출석 점수·이력 조회를 인증 client에 연결했습니다. 공지와 사전 보고 화면 및 인증 응답 이름을 연결했습니다. 팀 사진 업로드·제출과 푸시 기기 등록·해제는 어댑터·Hilt 조립까지 완료했고 실제 팀원 선택·Firebase 연결은 후속입니다. My 기수·역할·직군·지각 면제권·알림·문의는 UI를 유지하고 미확인 계약의 연결만 비워 둡니다.

Login Feature는 Google Credential Manager의 ID 토큰과 nonce를 메모리의 App 콜백으로 전달합니다. App은 YAPP 인증 결과를 해석해 세션을 저장하거나 Signup Feature로 이동합니다. Signup Feature는 가입 토큰을 저장하지 않고 이름·휴대폰 입력, E.164 검증, 서버 제출·완료 전환과 내부 뒤로 가기·입력 복원을 소유합니다. 시작 시 세션 복원은 App이 소유합니다.

App은 네 탭·공지 ID·설정 화면 이동을 소유합니다. 홈과 일정의 ViewModel은 Repository 결과를 표시하고 예시 값은 `src/debug` Preview에서만 사용합니다. My ViewModel은 출석 점수와 이력 API 요청·페이지·오류를 관리하며, 별도 이력 화면 이동은 App의 저장 가능한 상태입니다. 기능별 화면은 Preview와 테스트에서 독립적으로 확인합니다.

임시 비공개 테스트 구현은 `release/v1.0.1`에 보존합니다. 브랜치 운영은 [CONTRIBUTING.md](../../CONTRIBUTING.md)를 따릅니다.

## 파일 배치

- Android 모듈의 Kotlin 소스는 `src/main/java/<패키지 경로>/`에 둡니다. 순수 Kotlin/JVM Domain 소스는 `domain/src/main/kotlin/<패키지 경로>/`에 둡니다.
- 단위 테스트는 `src/test/`, 기기 테스트는 `src/androidTest/`의 해당 패키지에 둡니다.
- App에는 Android 진입점·전체 내비게이션·객체 조립을, Feature에는 화면·ViewModel·UiState와 기능별 UI 변환을 둡니다.
- Domain에는 모델·정책·Repository 계약·필요한 UseCase를, Data에는 Repository 구현·DataSource·DTO·Domain 변환을 둡니다.
- 각 Android 모듈의 Hilt 의존성 주입 파일(`@Module`, `@Binds`, `@Provides` 선언 등)은 모듈 루트의 `di/` 패키지에 둡니다. 예를 들어 App은 `com.yapp.plus.di`, 로그인 Feature는 `com.yapp.plus.feature.login.di`를 사용합니다.
- 독립 UI 컴포넌트 구현은 모듈 루트의 `components/` 패키지에 둡니다. 필요하면 `components/auth/`, `components/button/`처럼 하위 패키지로 구분합니다. Route·Screen과 화면 상태 변환은 Feature의 기능 패키지에 두고 `components/`와 분리합니다.
- Designsystem의 테마는 `core/designsystem/src/main/java/com/yapp/plus/core/designsystem/theme/`에 있습니다. 공통 컴포넌트도 이 모듈의 `components/`에서 관리합니다.
- 인증의 공통 표현은 Designsystem의 `components/auth/`에 둡니다. 표시값과 콜백만 받고 Feature 상태·목적지·인증 정책을 알지 못합니다. 기능별 문구·로그인 공급자 로고·상태 변환은 Feature가 소유합니다.
- Preview 전용 소스·리소스는 각 모듈의 `src/debug/`에 둡니다. Preview 패키지 경로는 대응하는 `components/` 하위 경로와 맞춥니다. `:core:preview`도 debug 소스에만 provider·fixture를 제공하며 Designsystem·Feature·Domain·Data·Navigation에 의존하지 않습니다. 소비 모듈은 필요한 경우에만 `debugImplementation(project(":core:preview"))`으로 연결합니다. `src/main`은 debug 타입을 참조하지 않고 앱 release 의존성 그래프에 Preview 모듈을 포함하지 않습니다.
- 사용자 문구는 해당 Android 모듈의 `src/main/res/values/strings.xml`에 둡니다.
- 범용 파일·모듈을 미리 만들지 않습니다. 여러 사용처의 실제 공통 책임이 확인되면 공통화합니다.

## 모듈 변경 절차

모듈을 추가하거나 옮길 때 실제 책임·사용처·계약·호출부를 확인합니다. 파일·패키지를 배치하고 `settings.gradle.kts`에 등록한 뒤 [아키텍처의 의존성 방향](architecture.md)과 App의 객체 조립을 확인합니다. 재사용할 Fake·테스트 도구는 필요할 때 테스트 의존성으로만 연결합니다. 영향받는 모듈과 호출부를 검증하고 이 문서, `AGENTS.md`, `README.md`의 상태를 함께 갱신합니다. 없는 모듈이나 검사 도구의 명령을 실행했다고 보고하지 않습니다.

## 인증 세션 자동 검증

- App 내비게이션 UI 테스트는 debug 전용 Hilt 진입점 `NavigationTestActivity`를 사용합니다. 일반 `ComponentActivity`는 Hilt ViewModel을 생성하지 못하며 이 테스트 Activity는 release 소스에 포함하지 않습니다.

- App의 `AppAuthViewModelTest`는 Fake Repository로 성공 전 세션 보존, 실패·재시도, 중복·교차 요청 방지와 취소 후 로딩 해제를 검증하며 8건을 통과했습니다.
- App의 `AccountDeletionUiTest` 4건은 주입한 콜백으로 회원 탈퇴 확인·복원·진행·오류 재시도를, `SessionOperationNavigationUiTest` 3건은 성공 콜백 후 로그인 복귀·Main back stack 제거와 My 이력 화면의 상태 복원·시스템 뒤로 가기를 확인합니다. API 35 에뮬레이터에서 7건을 통과했습니다. 실제 API 연결은 구현됐지만 이 UI 테스트는 모의 콜백을 사용하므로 서버 E2E 검증으로 간주하지 않습니다.
- `MyPageUiTest`, `ConnectedApisUiTest`, `UnconnectedFeaturesUiTest`, 위 두 테스트 클래스와 `ReleaseDesignUiTest.settingsActionsRemainReachableOnCompactScreen`을 함께 실행해 API 35에서 UI 테스트 18건을 통과했습니다. 공지의 명시적 읽음 확인, 보고 선택·사유, 이름 표시와 미확인 회원 필드, My 내역·규칙·빈 화면·재시도를 검증합니다. 화면 데이터와 제출 콜백은 테스트 대역입니다.

- Network의 `TokenStorePersistenceTest`와 `RestoredSessionAuthenticationTest`는 저장소 재생성 후 복원, 첫 요청의 복원 대기, 401 갱신·재시도, 동시 20개 요청의 갱신 1회 공유, 저장 실패·취소와 세션 교체 경계를 검증합니다. `SessionAuthenticationTest`는 원래 요청의 method·body·query·headers 보존과 기존 인증 경계를 검증합니다.
- Data의 `SessionRestartInstrumentedTest`는 실제 `SessionTokenLocalDataSource`·DataStore·Android Keystore를 사용합니다. 테스트 APK에서 토큰 회전 저장 → 프로세스 종료 후 복원·삭제 → 다시 종료 후 빈 세션 복원을 검증합니다. 테스트 토큰은 가짜 값이고 일반 앱의 세션 파일이나 실제 서버를 사용하지 않습니다.
- 프로세스 검증은 아래 스크립트가 세 단계를 각각 별도 instrumentation 프로세스로 실행하고 PID가 서로 다른지 확인합니다. 명시적인 단계 인자가 없는 일반 instrumentation 실행에서는 해당 세 단계가 생략됩니다. `adb`가 PATH에 없으면 스크립트의 `--adb` 옵션으로 경로를 전달합니다.

```bash
./gradlew :core:network:testDebugUnitTest :data:testDebugUnitTest :data:installDebugAndroidTest
python3 scripts/verify_session_restart.py --serial emulator-5554
```

기기 serial은 실행 중인 기기의 값으로 바꿉니다. 실제 YAPP 서버의 초기 세션·토큰 회전 검증에는 별도의 유효한 테스트 토큰 쌍이 필요합니다.
