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
- DTO는 Data 내부에 두고 Domain 변환도 Data에서 처리합니다.
- Domain에서 UI 모델·리소스로 바꾸는 책임은 Feature에 둡니다. 실제로 여러 Feature가 공유할 때 의존성 방향을 유지하며 공통화합니다.
- UseCase는 정책·Repository 조합·재사용이 필요한 경우에 추가하고 단순 전달용 클래스는 일괄 생성하지 않습니다.

## 객체 조립과 화면 이동

- 각 모듈은 자신의 객체 구성 정보를 제공하고 전체 조립은 App에서 담당합니다. Android DI는 Hilt를 사용하며 App이 `@HiltAndroidApp` 진입점을 소유합니다.
- 화면 목적지는 타입으로 표현합니다. 전체 이동은 App에서 조율하고 Feature 경계를 유지합니다.
- 하위 UI 컴포넌트는 콜백으로 이동을 요청하며, 다른 화면의 구현 대신 필요한 식별자와 입력을 전달합니다.
- 시스템 뒤로 가기와 전체 내비게이션은 App이 소유합니다. 공통 레이아웃은 뒤로 버튼의 콜백만 전달하고 `BackHandler`나 목적지 선택을 포함하지 않습니다. 시스템 바 inset도 App에서 공급합니다.

업무 정책·상태 전이·매핑·오류 처리의 경계 조건을 기존 테스트 도구로 검증합니다. Repository 테스트 대역은 가능한 경우 Fake를 사용합니다. 여러 모듈에서 재사용할 Fake·테스트 도구가 생기면 테스트 의존성으로만 연결합니다. 변경 모듈과 영향받는 호출부부터 검증하고 실제 결과를 기록합니다.

## HTTP 인증 세션 갱신

- 인증된 API 요청이 401을 받으면 refresh token으로 갱신하고 원래 요청을 한 번만 재시도합니다. method·body·query·기존 헤더를 유지하고 Authorization을 새 access token으로 바꿉니다.
- 동시에 발생한 요청은 같은 갱신 결과를 공유합니다. 갱신이 끝난 뒤 이전 토큰으로 도착한 401도 새 결과를 사용해 중복 갱신을 막습니다.
- 갱신 API의 401은 해당 세션을 거절하고, 일시적인 네트워크·서버 실패는 기존 세션을 보존합니다. 재시도 요청은 다시 갱신하지 않습니다.
- 요청 취소는 호출자에게 전파하되 다른 요청의 갱신은 취소하지 않습니다. 다른 세션으로 바뀌면 이전 결과로 토큰을 덮어쓰거나 요청을 재시도하지 않습니다.
- Network의 `RefreshCoordinator`가 동시 갱신과 재시도를 조율합니다. Data의 갱신 API 구현과 App의 singleton 조립은 후속 PR입니다.

## 인증 세션 저장

- YAPP access/refresh token 쌍은 DataStore의 불변 레코드와 `updateData`로 함께 저장합니다. Network의 `TokenPersistence` 계약을 Data의 로컬 DataSource가 구현하며, 직렬화·파일·암호화는 `:core:datastore`가 담당합니다.
- Keystore AES-256-GCM 암호화와 `noBackupFilesDir`를 사용하고 API base URL을 namespace로 파일을 분리합니다. 파일 손상·복호화 불가는 빈 세션으로 복구하고 일반 I/O 실패는 전파합니다. 토큰 원문은 로그에 남기지 않습니다. Google ID 토큰·nonce는 저장하지 않습니다.
- `TokenStore`는 첫 조회의 복원을 기다리고 교체·삭제를 같은 Mutex로 직렬화합니다. 저장 완료 후 메모리를 갱신하고 저장 실패는 전파합니다. 저장 도중 호출자가 취소돼도 디스크·메모리 반영 구간은 완료합니다. 늦은 갱신 결과는 조건부 교체로 다른 세션을 덮어쓰지 못하게 합니다.
- App의 singleton 조립, 첫 인증 요청의 복원 대기와 401 갱신·재시도 연결은 후속입니다. 같은 namespace의 DataStore 인스턴스는 프로세스 내 하나로 유지해야 합니다.
