---
authority: canonical
status: active
last_verified: 2026-10-20
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
- `RepositoryImpl`은 기능별 DataSource 인터페이스를 생성자로 주입받고 구체 구현을 내부에서 만들지 않습니다. DataSource 공통 모델·오류 계약은 Data가 소유하며 공급자별 DTO·SDK 타입은 어댑터 내부로 제한합니다. App이 기능별 인터페이스에 공급자 어댑터를 조립하고 Fake는 테스트에서 사용합니다. 현재 일정·공지·출석 검증은 Supabase 어댑터를 사용하며 전환 적용 상태는 [아키텍처](architecture.md#데이터-공급자-교체-경계)를 따릅니다.
- DTO는 Data 내부에 두고 Domain 변환도 Data에서 처리합니다.
- Domain에서 UI 모델·리소스로 바꾸는 책임은 Feature에 둡니다. 실제로 여러 Feature가 공유할 때 의존성 방향을 유지하며 공통화합니다.
- UseCase는 정책·Repository 조합·재사용이 필요한 경우에 추가하고 단순 전달용 클래스는 일괄 생성하지 않습니다.

## 객체 조립과 화면 이동

- 각 모듈은 자신의 객체 구성 정보를 제공하고 전체 조립은 App에서 담당합니다. Android DI는 Hilt를 사용하며 App이 `@HiltAndroidApp` 진입점을 소유합니다.
- 화면 목적지는 타입으로 표현합니다. 전체 이동은 App에서 조율하고 Feature 경계를 유지합니다.
- 하위 UI 컴포넌트는 콜백으로 이동을 요청하며, 다른 화면의 구현 대신 필요한 식별자와 입력을 전달합니다.
- 시스템 뒤로 가기와 전체 내비게이션은 App이 소유합니다. 공통 레이아웃은 뒤로 버튼의 콜백만 전달하고 `BackHandler`나 목적지 선택을 포함하지 않습니다. 시스템 바 inset도 App에서 공급합니다.

## MVP 인증·공지 계약

- `AuthRepository`는 Google credential 교환, 현재 세션 조회·복원, 로그아웃을 제공합니다. `GoogleLoginCredential`은 ID 토큰과 원본 nonce만 전달하며 영구 저장·로그 출력을 금지합니다. `AuthSession.userId`는 형식을 해석하지 않는 사용자 식별자이며 Supabase 사용자 ID와 미래 YAPP 회원 ID의 대응은 후속 계약입니다.
- 인증 세션이 없는 조회는 `null`, 인증·복원 요청 자체의 실패는 예외로 구분합니다. Google 토큰 발급만으로 인증 성공 UiState를 만들지 않습니다. Data가 인증 제공자의 교환을 완료한 뒤 반환한 세션으로 App이 인증 흐름을 조율합니다.
- `NoticeRepository`는 운영·세션 공지를 유형별 또는 전체 목록과 상세로 조회합니다. 목록은 게시 시각 내림차순이며, 상세에서 조회할 수 있는 공지가 없으면 `null`을 반환합니다. 공통 `Notice`는 홈의 `Long` 식별자, 제목, 본문, 유형과 게시 시각을 제공합니다. 날짜·유형 문구와 홈·게시판 UI 모델 변환은 각 Feature 책임입니다.
- 인증·공지 Data 구현과 App Hilt 연결은 완료했고 Feature 연결은 후속입니다. 기존 Login Feature의 credential 타입을 Domain 타입으로 옮기는 작업과 게시판 UI 분류의 정책 대응도 아직 적용하지 않았습니다. 공지의 전송 DTO·본문 표현 형식·페이지 처리 같은 서버 세부 계약은 Domain 모델만으로 확정하지 않습니다.

Supabase 출석 검증은 기존 `AttendanceDataSource` 계약의 정수 코드를 네 자리 ASCII 숫자 문자열로 왼쪽 0 채움한 뒤 인증된 `POST /rest/v1/rpc/mvp_verify_attendance`에 `p_session_id`·`p_code`만 보냅니다. 성공은 요청 세션과 일치하는 단일 행의 `verified=true`와 유효한 `checked_in_at`으로 확인하며 시각·사용자는 DB가 정합니다. REST helper는 조회와 POST 모두 Supabase 세션의 401 복원·갱신 뒤 원래 요청을 한 번 재시도합니다. PostgREST 오류의 정확한 `INVALID_ATTENDANCE_CODE` marker만 코드 오류로 매핑하고 다른 오류·응답 검증 실패는 일반 `AttendanceException`으로 정제하며 취소는 전파합니다. MVP migration에 보고 backend가 없어 `submitReport`는 요청 없이 일반 실패를 반환하고 YAPP 보고 어댑터는 보존합니다. 보고 UI의 MVP 흐름 정리는 후속입니다.

## HTTP 인증 세션 갱신

이 절은 기존 YAPP API 세션에 적용합니다. MVP의 Supabase 인증 세션은 [데이터 공급자 교체 경계](architecture.md#데이터-공급자-교체-경계)에 따라 분리합니다.

- API 서버의 인증된 요청이 401을 받으면 refresh token으로 `POST /api/auth/session-renewals`를 호출하고, 성공한 경우 원래 요청을 한 번만 재시도합니다. method·body·query·기존 헤더를 유지하고 Authorization을 새 access token으로 교체합니다. 재시도도 401이면 추가 갱신하지 않습니다.
- 동시에 전송한 요청은 같은 갱신 작업과 결과를 공유합니다. 갱신 완료 뒤 늦게 도착하는 이전 토큰의 401도 그 결과를 사용합니다. 일시적인 실패도 같은 요청 집합에서 공유하고, 이후 새 요청은 다시 갱신을 시도할 수 있습니다.
- access/refresh token은 하나의 불변 토큰 쌍으로 교체합니다. 갱신 API의 401은 해당 토큰 쌍을 제거하고, 네트워크·서버 오류나 올바르지 않은 성공 응답은 기존 세션을 보존하며 호출자에게 실패를 전달합니다.
- 개별 요청의 취소는 호출자에게 전파하되 다른 요청이 기다리는 갱신을 취소하지 않습니다. 로그아웃·새 로그인으로 바뀐 세션을 이전 갱신 결과로 덮어쓰거나 다른 계정의 토큰으로 원래 요청을 재시도하지 않습니다.
- Network가 HTTP 인증과 갱신 조율을, Data가 갱신 API 호출과 내부 DTO를 소유합니다. App은 토큰 저장소와 인증 client를 singleton으로 조립하고, 갱신에는 인증 인터셉터가 없는 별도 client를 사용합니다. 다른 origin에는 토큰을 붙이거나 갱신하지 않습니다.
- YAPP access/refresh token 쌍은 DataStore에 함께 저장합니다. Network의 `TokenPersistence` 계약을 Data의 `SessionTokenLocalDataSource`가 구현하고, 실제 직렬화·암호화·파일 관리는 `:core:datastore`가 담당합니다. App이 저장소와 client를 singleton으로 조립합니다. 첫 인증 요청은 저장된 세션의 비동기 복원을 기다립니다.
- 로그인 결과는 suspend `TokenStore.updateTokens`로 공급합니다. 갱신 결과의 조건부 교체와 세션 삭제는 같은 Mutex로 직렬화하고, 저장이 끝난 뒤 메모리 상태를 갱신합니다. 저장 도중 호출자가 취소돼도 디스크·메모리 반영 구간은 끝까지 완료합니다. 저장 실패는 호출자에게 전파하며 성공으로 처리하지 않습니다.
- `:core:datastore`는 DataStore 1.2.1의 단일 불변 레코드와 `updateData`를 사용합니다. Android Keystore의 AES-256-GCM 키로 암호화하고 `noBackupFilesDir`에 저장합니다. API base URL별 파일을 분리해 개발·운영·모킹 환경의 세션을 섞지 않습니다. 파일 손상·복호화 불가는 빈 세션으로 복구하고 일반 I/O 실패는 전파합니다. 토큰과 직렬화 원문은 로그에 남기지 않습니다.
- 저장된 세션이 없으면 인증 토큰을 공급하지 않습니다. `local.properties` access-token 주입 경로는 제거했습니다. Google ID 토큰·nonce는 이 저장소에 넣지 않습니다. 서버의 초기 YAPP 로그인 세션 발급 API 연결과 인증 만료 시 화면 이동은 후속입니다.

업무 정책·상태 전이·매핑·오류 처리의 경계 조건을 기존 테스트 도구로 검증합니다. Repository 테스트 대역은 가능한 경우 Fake를 사용합니다. 여러 모듈에서 재사용할 Fake·테스트 도구가 생기면 테스트 의존성으로만 연결합니다. 변경 모듈과 영향받는 호출부부터 검증하고 실제 결과를 기록합니다.
