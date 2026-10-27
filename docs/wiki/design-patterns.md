---
authority: canonical
status: active
last_verified: 2026-10-27
---

# 설계 패턴

계층 경계는 [아키텍처](architecture.md), Kotlin 서식은 [코딩 컨벤션](coding-conventions.md)을 따릅니다.

## Route와 Content

- Route는 ViewModel 연결, 생명주기에 맞춘 상태 수집과 App이 제공하는 화면 이동 콜백 연결을 처리합니다.
- Content는 상태와 콜백을 받아 화면을 그립니다. 하위 UI 컴포넌트는 Repository·DataSource를 호출하거나 화면 이동을 직접 조립하지 않습니다.
- 펼침 여부 같은 일시적인 UI 상태는 Composable에서, 업무 처리·공유·복원이 필요한 화면 상태는 ViewModel에서 관리합니다.
- 공통 텍스트·버튼·색상·타이포그래피는 Designsystem에서 제공하고 반복되는 변형은 공통 컴포넌트의 variant로 추가합니다.

## Preview

- 컴포넌트별 Preview를 `src/debug`에 두고 화면 연결 없이 표시값과 콜백으로 렌더링합니다.
- enum 전체 값은 `:core:preview`의 `EnumPreviewParameterProvider`를 상속해 제공합니다. 입력 예시는 `TextInputPreviewParameterProvider`의 범용 fixture를 재사용하고 필요한 예시 값을 전달할 수 있습니다.
- 범용 fixture는 UI 라이브러리의 상태 타입·Feature enum·업무 검증 규칙을 포함하지 않습니다. fixture를 실제 UI 상태로 바꾸는 책임은 소비 모듈의 debug Preview에 둡니다.

## ViewModel과 요청

- 흐름은 `사용자 입력 → ViewModel → Repository/UseCase → UiState → Compose`입니다.
- ViewModel은 읽기 전용 `StateFlow<UiState>`를 노출하고 Activity·Context를 보관하지 않습니다.
- 로딩·실패·재시도 상태를 명시합니다. 여러 요청이 있는 화면에서는 요청별 상태와 중복 요청 처리 방식을 정합니다.
- 중요한 처리 결과는 UI 상태로 표현합니다. 화면 이동·Snackbar 같은 일회성 효과와 구분하고 유실 가능한 효과만으로 전달하지 않습니다.
- 업무 정책은 Domain에서 처리하고 ViewModel은 화면 상태와 요청을 조율합니다.
- 비동기 작업은 Coroutines·Flow를 사용하며 코루틴 취소 예외를 전파합니다.
- 실패 표현과 UI 오류 변환 방식을 일관되게 사용합니다. 프로젝트 오류 타입은 실제 API 계약과 요구사항에 맞춰 정의합니다.

## Repository와 DataSource

- Repository 계약은 Domain, 구현은 Data에 둡니다. 사용하는 책임을 기준으로 계약을 나누고 관련 없는 기능을 거대한 인터페이스에 모으지 않습니다.
- 원격·로컬 데이터 접근은 DataSource 경계로 분리합니다.
- `RepositoryImpl`은 기능별 DataSource 인터페이스를 생성자로 주입받고 구체 구현을 내부에서 만들지 않습니다. DataSource 공통 모델·오류 계약은 Data가 소유하며 공급자별 DTO·SDK 타입은 어댑터 내부로 제한합니다. App이 기능별 인터페이스에 공급자 어댑터를 조립하고 Fake는 테스트에서 사용합니다. 현재 일정·출석 검증·보고는 YAPP 서버 어댑터를 사용하며 적용 상태는 [아키텍처](architecture.md#데이터-공급자-교체-경계)를 따릅니다.
- DTO는 Data 내부에 두고 Domain 변환도 Data에서 처리합니다.
- Domain에서 UI 모델·리소스로 바꾸는 책임은 Feature에 둡니다. 실제로 여러 Feature가 공유할 때 의존성 방향을 유지하며 공통화합니다.
- UseCase는 정책·Repository 조합·재사용이 필요한 경우에 추가하고 단순 전달용 클래스는 일괄 생성하지 않습니다.

## 객체 조립과 화면 이동

- 각 모듈은 자신의 객체 구성 정보를 제공하고 전체 조립은 App에서 담당합니다. Android DI는 Hilt를 사용하며 App이 `@HiltAndroidApp` 진입점을 소유합니다.
- 화면 목적지는 타입으로 표현합니다. 전체 이동은 App에서 조율하고 Feature 경계를 유지합니다.
- 하위 UI 컴포넌트는 콜백으로 이동을 요청하며, 다른 화면의 구현 대신 필요한 식별자와 입력을 전달합니다.
- 시스템 뒤로 가기와 전체 내비게이션은 App이 소유합니다. 공통 레이아웃은 뒤로 버튼의 콜백만 전달하고 `BackHandler`나 목적지 선택을 포함하지 않습니다. 시스템 바 inset도 App에서 공급합니다.

### 가입 입력 흐름

- `:feature:signup`은 단일 `SignUpRoute`와 ViewModel로 이름·휴대폰 입력과 내부 뒤로 가기를 소유합니다. Route는 Feature 안에서 `hiltViewModel()`로 ViewModel을 가져옵니다.
- 입력값과 현재 단계는 `SavedStateHandle`로 유지·복원합니다. 이름이 공백이 아니면 휴대폰 입력으로 이동합니다.
- 가입 토큰은 App 메모리에만 유지하고 Navigation·`SavedStateHandle`에 저장하지 않습니다. Signup ViewModel은 입력 검증·요청 중복 방지·실패 재시도와 서버 성공 결과를 관리합니다. 이름·전화번호는 `POST /api/users`에 제출하고 성공 응답의 YAPP 세션을 저장한 뒤 완료 화면에서 메인으로 이동합니다. 저장된 완료 단계는 휴대폰 입력으로 복원합니다.
- Data는 가입 `fieldErrors`를 Domain의 입력별 오류로 변환하고 Feature는 오류가 난 입력창과 안내 문구를 선택합니다. 이름만 오류이면 이름 화면에서 기존 전화번호로 재제출할 수 있는 상태를 `SavedStateHandle`에 보관해 전화번호 단계를 생략합니다. 전화번호를 바꾸면 이 상태를 해제하고, 두 필드가 모두 오류이면 이름 → 전화번호 순서로 수정합니다. 입력 오류는 해당 값을 바꾸면 지우며 요청 중에는 입력 변경·이동·중복 제출을 막습니다.

## Google 크레덴셜과 세션 작업

- `GoogleLoginCredential`은 ID 토큰과 원본 nonce를 전달하며 영구 저장·로그 출력을 금지합니다. Google 요청에는 nonce의 SHA-256 hex를 보내고 App 콜백에는 원본을 전달합니다.
- Credential Manager 요청은 중복 입력·취소·실패·재시도를 처리합니다. App은 `POST /api/auth/oidc/google/native-authentications` 결과에 따라 YAPP 세션을 저장해 메인으로 이동하거나, 일회용 가입 토큰을 메모리에 두고 Signup 목적지로 이동합니다. 서버 응답에 필요한 access/refresh token 쌍이 없으면 로그인 성공으로 처리하지 않습니다.
- 앱 시작 시 저장된 YAPP 세션 복원을 기다린 뒤 세션이 있으면 메인, 없으면 로그인으로 시작합니다. 서버 로그인 교환 실패는 로그인 재시도 오류로 표시합니다.
- 공지·회원 정보·출석 이력의 서버 계약이 준비되지 않은 Feature는 명시적인 `Unavailable` 상태를 표시합니다. 빈 서버 결과나 네트워크 오류로 오인하지 않도록 재시도를 노출하지 않습니다.
- 서버 인증 결과를 받은 뒤 `NavController`를 조작하는 구간은 `Dispatchers.Main.immediate`에서 실행합니다. 실제 신규 계정 E2E에서 확인한 메인 스레드 위반 예외를 이 경계에서 방지합니다.
- 설정의 로그아웃·통합 탈퇴는 `AppAuthViewModel`이 각각의 진행·오류 상태와 중복 실행 방지를 관리합니다. Domain Repository 결과가 성공일 때만 `hasSession`을 해제해 App 인증 내비게이션이 로그인 화면으로 돌아가며, 실패하면 현재 세션을 유지합니다. 로그아웃 오류는 설정에서 다시 요청할 수 있고 통합 탈퇴 오류는 확인 다이얼로그의 재시도 버튼으로 재요청합니다.
- Data는 로그아웃에 현재 refresh token을 담아 미인증 client로 요청하고, 통합 탈퇴는 access token을 자동 첨부·갱신하는 인증 client로 요청합니다. 두 작업 모두 성공 응답을 받은 뒤 token 쌍을 삭제합니다. UI의 안내·확인·로딩·오류 표시는 `:feature:setting`에 두고 실제 인증 요청과 목적지 변경은 App이 연결합니다. 계약과 안내 범위는 [제품 정책](product-policy.md#계정-기수-통합-탈퇴)을 따릅니다.

## HTTP 인증 세션 갱신

이 절은 YAPP API가 발급한 access/refresh token 쌍에 적용합니다.

- API 서버의 인증된 요청이 401을 받으면 refresh token으로 `POST /api/auth/session-renewals`를 호출하고, 성공한 경우 원래 요청을 한 번만 재시도합니다. method·body·query·기존 헤더를 유지하고 Authorization을 새 access token으로 교체합니다. 재시도도 401이면 추가 갱신하지 않습니다.
- 동시에 전송한 요청은 같은 갱신 작업과 결과를 공유합니다. 갱신 완료 뒤 늦게 도착하는 이전 토큰의 401도 그 결과를 사용합니다. 일시적인 실패도 같은 요청 집합에서 공유하고, 이후 새 요청은 다시 갱신을 시도할 수 있습니다.
- access/refresh token은 하나의 불변 토큰 쌍으로 교체합니다. 갱신 API의 401은 해당 토큰 쌍을 제거하고, 네트워크·서버 오류나 올바르지 않은 성공 응답은 기존 세션을 보존하며 호출자에게 실패를 전달합니다.
- 개별 요청의 취소는 호출자에게 전파하되 다른 요청이 기다리는 갱신을 취소하지 않습니다. 로그아웃·새 로그인으로 바뀐 세션을 이전 갱신 결과로 덮어쓰거나 다른 계정의 토큰으로 원래 요청을 재시도하지 않습니다.
- Network가 HTTP 인증과 갱신 조율을, Data가 갱신 API 호출과 내부 DTO를 소유합니다. App은 토큰 저장소와 인증 client를 singleton으로 조립하고, 갱신에는 인증 인터셉터가 없는 별도 client를 사용합니다. 다른 origin에는 토큰을 붙이거나 갱신하지 않습니다.
- YAPP access/refresh token 쌍은 DataStore에 함께 저장합니다. Network의 `TokenPersistence` 계약을 Data의 `SessionTokenLocalDataSource`가 구현하고, 실제 직렬화·암호화·파일 관리는 `:core:datastore`가 담당합니다. App이 저장소와 client를 singleton으로 조립합니다. 첫 인증 요청은 저장된 세션의 비동기 복원을 기다립니다.
- 로그인 결과는 suspend `TokenStore.updateTokens`로 공급합니다. 갱신 결과의 조건부 교체와 세션 삭제는 같은 Mutex로 직렬화하고, 저장이 끝난 뒤 메모리 상태를 갱신합니다. 저장 도중 호출자가 취소돼도 디스크·메모리 반영 구간은 끝까지 완료합니다. 저장 실패는 호출자에게 전파하며 성공으로 처리하지 않습니다.
- `:core:datastore`는 DataStore 1.2.1의 단일 불변 레코드와 `updateData`를 사용합니다. Android Keystore의 AES-256-GCM 키로 암호화하고 `noBackupFilesDir`에 저장합니다. API base URL별 파일을 분리해 개발·운영·모킹 환경의 세션을 섞지 않습니다. 파일 손상·복호화 불가는 빈 세션으로 복구하고 일반 I/O 실패는 전파합니다. 토큰과 직렬화 원문은 로그에 남기지 않습니다.
- 저장된 세션이 없으면 인증 토큰을 공급하지 않습니다. `local.properties` access-token 주입 경로는 제거했습니다. Google ID 토큰·nonce와 일회용 가입 토큰은 세션 저장소에 넣지 않습니다. Google 인증·가입의 성공 세션은 suspend `TokenStore.updateTokens`로 공급합니다. refresh 거절 뒤 로그인 화면으로 이동하는 인증 만료 UI는 후속입니다.

업무 정책·상태 전이·매핑·오류 처리의 경계 조건을 기존 테스트 도구로 검증합니다. Repository 테스트 대역은 가능한 경우 Fake를 사용합니다. 여러 모듈에서 재사용할 Fake·테스트 도구가 생기면 테스트 의존성으로만 연결합니다. 변경 모듈과 영향받는 호출부부터 검증하고 실제 결과를 기록합니다.
