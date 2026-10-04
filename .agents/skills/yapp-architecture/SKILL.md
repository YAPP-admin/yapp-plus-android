---
name: yapp-architecture
description: YAPP 출석 Android 앱에서 책임을 배치하거나 계층 경계를 변경할 때 승인된 MVVM, UDF, Clean Architecture와 의존성 방향을 적용한다.
---

# YAPP Architecture

[AGENTS.md](../../../AGENTS.md)의 승인된 구조를 기준으로 변경 책임과 의존성을 결정한다.
구체적인 파일 배치는 [yapp-module-structure](../yapp-module-structure/SKILL.md)를 참고한다.

## 책임과 의존성

- App은 진입점, 전체 내비게이션과 의존성 조립을 소유한다.
- Feature는 Compose 화면, ViewModel, UiState와 기능 흐름을 소유한다.
- Domain은 순수 Kotlin 모델, 업무 정책, Repository 계약과 필요한 UseCase를 소유한다.
- Data는 Repository 구현, 원격·로컬 DataSource, DTO와 Domain 매핑을 소유한다.
- Designsystem은 공통 테마·색상·타이포그래피·Compose 컴포넌트를 소유한다.
- Network는 HTTP 클라이언트와 공통 통신 설정을 소유한다.
- Feature는 Domain·Designsystem을 사용한다. Data 구현과 다른 Feature에 직접 의존하지 않는다.
- Data는 Domain·Network와 필요한 저장 모듈을 사용한다.
- Domain은 Android·Compose·HTTP 클라이언트·DB 구현에 의존하지 않는다.
- Designsystem은 App·Feature·Domain·Data를 알지 못한다. 순환 의존성을 만들지 않는다.

## 정책과 표현

- MVVM + UDF를 사용한다. Compose는 상태를 표시하고 입력을 전달한다.
- ViewModel은 화면 상태와 요청을 조율한다. 업무 정책은 Domain에 둔다.
- UseCase는 정책·Repository 조합·재사용이 필요할 때 추가한다. 단순 전달용으로 일괄 생성하지 않는다.
- Domain의 정책·UseCase를 Data 구현으로 옮기지 않는다. 데이터 접근은 Domain 계약을 통해 요청한다.
- DTO는 Data 내부에 두고 Domain 모델로 변환한다.
- Domain → UI 모델·리소스 변환은 해당 Feature에서 처리한다.
  여러 Feature가 실제로 공유하는 변환은 공통화하되 승인된 의존성 방향을 유지한다.
- 출석 정책과 API 계약은 확인된 요구사항을 따른다. 화면 표시와 서버 확정 결과를 구분한다.

## 경계 변경 확인

변경 전 호출부, Gradle 의존성, 객체 조립 위치와 관련 테스트를 확인한다.
변경 후 계약 호환성·의존성 방향을 검토하고 영향받는 모듈부터 검증한다.
새 모듈은 실제 책임이 생길 때 추가하며 AGENTS.md와 README의 상태를 함께 갱신한다.
