---
authority: canonical
status: active
last_verified: 2026-10-27
---

# 아키텍처

MVVM과 단방향 상태 흐름(UDF)을 사용하고, 비즈니스 규칙에 필요한 범위에서 Clean Architecture를 적용합니다. 실제 모듈 상태와 패키지는 [모듈 구조](module-structure.md)를 확인합니다.

## 책임과 의존성

| 계층 | 소유하는 책임 |
| --- | --- |
| App | 앱 진입점, 전체 내비게이션, 의존성 조립 |
| Feature | Compose 화면, ViewModel, UiState, 기능 흐름 |
| Domain | 순수 Kotlin 모델, 업무 정책, Repository 계약, 필요한 UseCase |
| Data | Repository 구현, 원격·로컬 DataSource, DTO와 Domain 매핑 |
| Designsystem | 공통 테마, 색상, 타이포그래피, Compose 컴포넌트 |
| Network | HTTP 클라이언트와 공통 통신 설정 |

Android DI는 Hilt를 사용하며 App이 `@HiltAndroidApp` 진입점을 소유합니다. Network는 Ktor를 사용하고 JSON은 Kotlin serialization으로 처리합니다. 서버 계약이 정해지기 전에는 API별 요청·응답 모델이나 통신 정책을 추가하지 않습니다.

```text
:app → :feature:* / :data / :core:network / :core:designsystem
:feature:* → :domain / :core:designsystem
:data → :domain / :core:network / 필요 시 :core:datastore
:domain → Kotlin 및 필요한 순수 Kotlin 라이브러리
```

- Feature는 Data 구현이나 다른 Feature에 직접 의존하지 않습니다. 전체 화면 이동과 Domain 계약에 대한 Data 구현 연결은 App에서 조율합니다.
- 메인 화면의 홈·일정·게시판·My는 각각 `:feature:main:home`, `:feature:main:schedule`, `:feature:main:board`, `:feature:main:my` 독립 Feature 모듈로 둡니다. 설정은 `:feature:setting`에 둡니다. App이 이들을 조립하고 공통 메인 내비게이션, 탭 선택과 설정 하위 화면 상태를 소유합니다. 탭의 공통 시각 컴포넌트는 Designsystem이 제공하며 Feature 간 의존성은 두지 않습니다.
- Domain은 Android, Compose, HTTP 클라이언트, DB 구현에 의존하지 않습니다. 업무 정책과 UseCase를 Data 구현으로 옮기지 않습니다.
- Designsystem은 App, Feature, Domain, Data를 알지 못합니다. Network와 Datastore는 Feature의 화면·상태를 알지 못합니다. 순환 의존성을 만들지 않습니다.
- DTO는 Data 내부에 두고 Domain 모델로 변환합니다. Domain에서 UI 모델·리소스로 바꾸는 책임은 해당 Feature에 둡니다. 여러 Feature가 실제로 공유할 때 의존성 방향을 유지하며 공통화합니다.

## 데이터 공급자 교체 경계

호출 경계는 `Feature → Domain Repository 계약 → Data RepositoryImpl → 생성자 주입한 기능별 DataSource 인터페이스 → YAPP 서버 구현`입니다.

- `RepositoryImpl`은 기능별 DataSource 인터페이스를 생성자로 받습니다. DataSource의 공통 입출력과 서버 DTO는 Data가 소유하고 Domain에는 업무 모델과 Repository 계약만 노출합니다.
- App Hilt는 인증에 `AuthRemoteDataSource`, 일정에 `SessionScheduleRemoteDataSource`, 출석 인증·보고에 `AttendanceRemoteDataSource`를 연결합니다. 인증 DataSource에는 로그인·로그아웃용 미인증 client와 통합 탈퇴용 인증 client를 전달합니다. Fake는 같은 DataSource 계약을 구현하는 테스트 대역으로만 사용합니다.
- Network는 Ktor HTTP client와 토큰 복원·401 갱신 조율을 소유하고 Data는 YAPP 갱신 API·DTO와 `TokenPersistence`의 로컬 구현을 제공합니다. 실제 암호화 저장은 `:core:datastore`가 담당합니다.
- Login Feature는 Google Credential Manager 크레덴셜을 획득합니다. App은 메모리 콜백으로 받은 ID 토큰·원본 nonce를 Data의 `AuthRepository`에 전달하고, 인증 결과의 YAPP 토큰 쌍을 `TokenStore`에 저장합니다. Signup Feature는 일회용 가입 토큰·이름·E.164 전화번호를 서버에 제출합니다.
- 아직 연결하지 않은 공지·회원 프로필·출석 이력은 Feature가 준비 중 상태를 표시합니다. 설정의 로그아웃·통합 탈퇴 콜백은 App 인증 ViewModel이 Domain Repository를 호출하고 요청 상태·성공·오류를 소유합니다. 성공 시 세션 상태를 갱신해 App이 로그인 화면으로 이동하고, 실패 시 세션을 보존합니다.

적용 상태: Google 네이티브 로그인·가입과 YAPP 세션 저장·복원·갱신, 로그아웃·통합 탈퇴, 일정·출석 검증·보고를 App에 조립했습니다. 공지·회원 조회는 [제품 정책](product-policy.md#현재-서버-연결)을 따릅니다.

## 정책과 표현

- 흐름은 `사용자 입력 → ViewModel → Repository/UseCase → UiState → Compose`입니다. Compose는 상태를 표시하고 입력을 전달하며, ViewModel은 화면 상태와 요청을 조율합니다.
- UseCase는 업무 규칙, 여러 Repository의 조합, 재사용이 필요할 때 추가합니다. 단순 전달용 클래스를 일괄 생성하지 않습니다.
- 출석 정책과 API 계약은 [제품 정책](product-policy.md)에 확인된 내용만 적용합니다. 화면 표시와 서버 확정 결과를 구분합니다.

경계 변경 전 호출부, Gradle 의존성, 객체 조립 위치와 관련 테스트를 확인합니다. 변경 후 계약 호환성과 의존성 방향을 검토하고 영향받는 모듈부터 검증합니다.
