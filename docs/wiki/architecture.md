---
authority: canonical
status: active
last_verified: 2026-10-04
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

```text
:app → :feature:* / :data / :core:designsystem
:feature:* → :domain / :core:designsystem
:data → :domain / :core:network / 필요 시 :core:datastore
:domain → Kotlin 및 필요한 순수 Kotlin 라이브러리
```

- Feature는 Data 구현이나 다른 Feature에 직접 의존하지 않습니다. 전체 화면 이동과 Domain 계약에 대한 Data 구현 연결은 App에서 조율합니다.
- Domain은 Android, Compose, HTTP 클라이언트, DB 구현에 의존하지 않습니다. 업무 정책과 UseCase를 Data 구현으로 옮기지 않습니다.
- Designsystem은 App, Feature, Domain, Data를 알지 못합니다. Network와 Datastore는 Feature의 화면·상태를 알지 못합니다. 순환 의존성을 만들지 않습니다.
- DTO는 Data 내부에 두고 Domain 모델로 변환합니다. Domain에서 UI 모델·리소스로 바꾸는 책임은 해당 Feature에 둡니다. 여러 Feature가 실제로 공유할 때 의존성 방향을 유지하며 공통화합니다.

## 정책과 표현

- 흐름은 `사용자 입력 → ViewModel → Repository/UseCase → UiState → Compose`입니다. Compose는 상태를 표시하고 입력을 전달하며, ViewModel은 화면 상태와 요청을 조율합니다.
- UseCase는 업무 규칙, 여러 Repository의 조합, 재사용이 필요할 때 추가합니다. 단순 전달용 클래스를 일괄 생성하지 않습니다.
- 출석 정책과 API 계약은 [제품 정책](product-policy.md)에 확인된 내용만 적용합니다. 화면 표시와 서버 확정 결과를 구분합니다.

경계 변경 전 호출부, Gradle 의존성, 객체 조립 위치와 관련 테스트를 확인합니다. 변경 후 계약 호환성과 의존성 방향을 검토하고 영향받는 모듈부터 검증합니다.
