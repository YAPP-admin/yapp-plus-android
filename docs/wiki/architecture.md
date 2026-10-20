---
authority: canonical
status: active
last_verified: 2026-10-20
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
- 메인 화면의 홈·일정·게시판·My는 각각 `:feature:main:home`, `:feature:main:schedule`, `:feature:main:board`, `:feature:main:my` 독립 Feature 모듈로 둡니다. App이 이들을 조립하고 공통 메인 내비게이션과 탭 선택 상태를 소유합니다. 탭의 공통 시각 컴포넌트는 Designsystem이 제공하며 Feature 간 의존성은 두지 않습니다.
- Domain은 Android, Compose, HTTP 클라이언트, DB 구현에 의존하지 않습니다. 업무 정책과 UseCase를 Data 구현으로 옮기지 않습니다.
- Designsystem은 App, Feature, Domain, Data를 알지 못합니다. Network와 Datastore는 Feature의 화면·상태를 알지 못합니다. 순환 의존성을 만들지 않습니다.
- DTO는 Data 내부에 두고 Domain 모델로 변환합니다. Domain에서 UI 모델·리소스로 바꾸는 책임은 해당 Feature에 둡니다. 여러 Feature가 실제로 공유할 때 의존성 방향을 유지하며 공통화합니다.

## 데이터 공급자 교체 경계

[비공개 테스트 MVP](product-policy.md#supabase-기반-비공개-테스트-mvp)의 데이터 공급자는 Supabase로 두고, 이후 YAPP 원본 서버로 교체할 수 있게 합니다. 호출 경계는 `Feature → Domain Repository 계약 → Data RepositoryImpl → 생성자 주입한 기능별 DataSource 인터페이스 → 공급자별 구현`입니다.

- `RepositoryImpl`은 기능별 DataSource 인터페이스를 생성자로 받습니다. 구체 DataSource를 내부에서 생성하거나 Supabase SDK·HTTP client를 직접 받아 공급자를 선택하지 않습니다.
- DataSource 인터페이스와 공급자에 독립적인 공통 입출력·오류 계약은 Data에 둡니다. Supabase·YAPP의 SDK 타입, HTTP 응답과 공급자별 DTO는 각 어댑터 내부에 둡니다. Domain에는 Repository 계약과 업무 모델만 노출합니다.
- App의 Hilt 조립에서 현재 Supabase 어댑터를 선택하고, 향후 확인된 YAPP 서버 계약의 어댑터로 바꿉니다. Feature와 Domain 계약은 공급자 교체를 위해 수정하지 않습니다. Fake는 같은 DataSource 계약을 구현하는 테스트 대역으로 사용합니다.
- Google ID 토큰과 원본 nonce는 Domain의 로그인 입력 모델로 전달하지만 인증 제공자의 access/refresh token은 Data에서 관리합니다. `AuthSession`은 토큰·프로필·제공자 SDK 타입 없이 불투명한 사용자 식별자만 노출합니다.
- YAPP 세션 토큰 저장·401 갱신과 Supabase 인증 세션은 공급자가 다른 별도 세션입니다. Supabase 토큰을 기존 YAPP API나 YAPP 세션 갱신 경로에 전달하지 않습니다.

적용 상태: 인증·공지 Domain 계약과 기존 세션 일정·출석 `RepositoryImpl`의 DataSource 인터페이스 주입을 구현했습니다. Supabase `AuthDataSource`·`AuthRepositoryImpl`과 App Hilt 연결, YAPP 세션과 분리한 암호화 토큰 저장소를 추가했습니다. App은 일정·공지·출석 검증의 Supabase 어댑터와 공지 Repository를 연결합니다. Login Feature의 Domain credential을 App에서 Supabase 세션으로 교환하고 앱 시작 시 복원합니다. 복원 실패에는 재시도를 제공하며 debug·release 모두 인증된 세션으로 메인에 진입합니다. 공지 Feature 연결은 후속입니다. 세션 DataSource는 Data 소유의 날짜·시간·상태가 명시된 공통 모델을 반환하며 공급자 응답 DTO를 노출하지 않습니다. YAPP 출석 HTTP 어댑터는 보존하며 Supabase 경로의 보고 호출은 backend가 없어 일반 출석 실패로 끝납니다. Supabase의 세션·공지·본인 출석 조회와 서버 출석 검증 계약은 [DB migration](../../supabase/migrations/20261020093715_mvp_test_sessions_notices_attendance.sql)에 추가했으며 실제 프로젝트에는 아직 적용하지 않았습니다. 남은 작업은 [이슈 #90](https://github.com/YAPP-admin/yapp-plus-android/issues/90)에 기록합니다. YAPP 초기 로그인·회원 식별자 대응처럼 미확정인 서버 계약을 이 경계만을 위해 만들지 않습니다.

Supabase의 `mvp_context`·`mvp_session_schedule` view는 호출자 권한과 RLS를 적용합니다. 출석 RPC는 비공개 함수가 `auth.uid()`와 DB 요청 시각으로 검증하고, 외부에는 `mvp_verify_attendance` invoker 함수만 노출합니다. 코드·허용 목록에는 클라이언트 읽기 권한을 주지 않습니다. 기수 정보는 설정 가능한 `null`로 시작하며 세션 조회를 막지 않습니다. 일정 어댑터는 `is_test=true` 세션만 조회하며 필수 기수 필드의 미설정 값은 테스트 전용 `0`으로 매핑합니다. 이 값은 공식 YAPP 기수가 아니며 현재 일정 UI에는 기수 번호를 표시하지 않습니다.

## 정책과 표현

- 흐름은 `사용자 입력 → ViewModel → Repository/UseCase → UiState → Compose`입니다. Compose는 상태를 표시하고 입력을 전달하며, ViewModel은 화면 상태와 요청을 조율합니다.
- UseCase는 업무 규칙, 여러 Repository의 조합, 재사용이 필요할 때 추가합니다. 단순 전달용 클래스를 일괄 생성하지 않습니다.
- 출석 정책과 API 계약은 [제품 정책](product-policy.md)에 확인된 내용만 적용합니다. 화면 표시와 서버 확정 결과를 구분합니다.

경계 변경 전 호출부, Gradle 의존성, 객체 조립 위치와 관련 테스트를 확인합니다. 변경 후 계약 호환성과 의존성 방향을 검토하고 영향받는 모듈부터 검증합니다.
