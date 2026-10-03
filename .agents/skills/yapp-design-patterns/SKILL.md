---
name: yapp-design-patterns
description: YAPP의 Compose 화면, ViewModel 상태·비동기 요청, Repository·DataSource, 객체 조립과 화면 이동을 구현하거나 리뷰할 때 승인된 패턴을 적용한다.
---

# YAPP Design Patterns

계층 경계는 [yapp-architecture](../yapp-architecture/SKILL.md),
Kotlin 서식은 [yapp-coding-conventions](../yapp-coding-conventions/SKILL.md)를 따른다.

## Route와 Content

- Route는 ViewModel 연결, 생명주기에 맞춘 상태 수집과 화면 이동을 처리한다.
- Content는 상태와 콜백을 받아 화면을 그린다.
  하위 UI 컴포넌트에서 Repository·DataSource를 호출하거나 직접 화면 이동을 조립하지 않는다.
- 펼침 여부 같은 일시적인 UI 상태는 Composable에서 관리한다.
- 업무 처리·공유·복원이 필요한 화면 상태는 ViewModel에서 관리한다.
- 공통 텍스트·버튼·색상·타이포그래피는 Designsystem에서 제공한다.
  반복되는 변형은 공통 컴포넌트의 variant로 추가한다.

## ViewModel과 요청

- 흐름은 `사용자 입력 → ViewModel → Repository/UseCase → UiState → Compose`다.
- ViewModel은 읽기 전용 `StateFlow<UiState>`를 노출한다.
  Activity나 Context를 보관하지 않는다.
- 로딩·실패·재시도 상태를 명시한다.
  여러 요청이 있는 화면에서는 요청별 로딩·오류 상태와 중복 요청 처리 방식을 정한다.
- 중요한 처리 결과는 UI 상태로 표현한다.
  화면 이동·Snackbar 같은 일회성 효과와 구분하고 유실 가능한 효과만으로 전달하지 않는다.
- 비즈니스 정책은 Domain에서 처리하고 ViewModel은 화면 상태와 요청을 조율한다.
- 비동기 작업은 Coroutines·Flow를 사용하며 코루틴 취소 예외를 전파한다.
- 실패 표현과 UI 오류 변환 방식을 일관되게 사용한다.
  프로젝트 오류 타입은 실제 API 계약과 요구사항에 맞춰 정의한다.

## Repository와 DataSource

- Repository 계약은 Domain, 구현은 Data에 둔다.
- 사용하는 책임을 기준으로 계약을 나눈다. 관련 없는 기능을 거대한 인터페이스에 모으지 않는다.
- 원격·로컬 데이터 접근은 DataSource 경계로 분리한다.
- DTO는 Data 내부에 두고 Domain 변환도 Data에서 처리한다.
- Domain → UI 모델·리소스 변환은 해당 Feature에서 처리한다.
  여러 Feature가 실제로 공유할 때 의존성 방향을 유지하며 공통화한다.
- UseCase는 정책·Repository 조합·재사용이 필요한 경우 추가한다.
  단순 전달용 클래스는 일괄 생성하지 않는다.

## 객체 조립과 화면 이동

- 각 모듈은 자신의 객체 구성 정보를 제공하고 전체 조립은 App에서 담당한다.
- DI 라이브러리는 아직 선택하지 않았다. 라이브러리 도입은 별도로 결정한다.
- 화면 목적지는 타입으로 표현한다. 전체 이동은 App에서 조율하고 Feature 경계를 유지한다.
- 하위 UI 컴포넌트는 콜백으로 이동을 요청한다.
  다른 화면의 구현 대신 필요한 식별자와 입력을 전달한다.

## 검증

- 업무 정책·상태 전이·매핑·오류 처리의 경계 조건을 기존 테스트 도구로 검증한다.
- Repository 테스트 대역은 가능한 경우 Fake를 사용한다.
- 여러 모듈에서 재사용할 Fake·테스트 도구가 생기면 공통화하고 테스트 의존성으로만 연결한다.
- 변경 모듈과 영향받는 호출부부터 검증한다.
  실제 명령·결과·남은 실패를 기록하고 코드 문제와 환경 문제를 구분한다.
