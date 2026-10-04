# YAPP 출석 앱 개발 워크플로우

## 프로젝트와 실행 기준

- YAPP 공식 출석 앱의 Android 저장소다. Kotlin과 Jetpack Compose를 사용한다.
- 승인된 아키텍처는 MVVM + 단방향 상태 흐름(UDF) + 필요한 범위의 Clean Architecture다.
- GitHub 저장소는 `YAPP-admin/yapp-plus-android`다. 브랜치·제목·라벨·변경량 정책의 기준은 [CONTRIBUTING.md](CONTRIBUTING.md)다.
- 프로젝트 Codex 기본 모델은 `.codex/config.toml`의 `gpt-6-luna`, 추론 수준 `max`다. 사용자가 선택한 세션 모델은 존중한다.
- 구현 요청은 필요한 이슈 작성, 구현, 검증, 커밋, 푸시와 PR 생성까지 진행한다. 사용자가 범위를 제한하면 그 범위를 따른다. 이미 승인된 작업을 다시 승인받지 않는다.
- 요청이 설계 논의나 조사에 한정되면 먼저 결과를 정리한다. 제품 정책·API 계약 등 구현에 필요한 정보가 없으면 해당 정보만 질문한다.

## 프로젝트 스킬과 승인된 규칙

아래 스킬은 사용자와 검토한 1~35번 규칙을 YAPP 구조에 맞춰 정리한 실행 기준이다.
작업에 해당하는 스킬을 읽고 구현·리뷰·검증에 적용한다.

| 작업 | 스킬 |
| --- | --- |
| 책임 배치·계층 경계·의존성 변경 | [yapp-architecture](.agents/skills/yapp-architecture/SKILL.md) |
| Kotlin·Gradle Kotlin 작성·리뷰 | [yapp-coding-conventions](.agents/skills/yapp-coding-conventions/SKILL.md) |
| 화면·상태·요청·Repository·객체 조립·화면 이동 | [yapp-design-patterns](.agents/skills/yapp-design-patterns/SKILL.md) |
| 파일 배치·패키지·모듈 추가·이동 | [yapp-module-structure](.agents/skills/yapp-module-structure/SKILL.md) |

- Kotlin 변경에는 coding-conventions를 항상 적용한다. 서식 기준은 `.editorconfig`에도 기록한다.
- 확장 함수는 허용한다. `private` 클래스는 금지하고 파일당 클래스·인터페이스는 하나로 둔다.
  sealed 타입과 관련 하위 타입은 같은 파일에 둘 수 있다.
- 제어문 중첩은 최대 2단계다. 함수는 15줄 초과 시 분리를 검토하고 다른 책임이 있으면 나눈다.
- 포맷터·정적 분석 플러그인과 DI 라이브러리는 별도로 선택한다.
- 다른 프로젝트 문서는 참고 자료다. 승인되지 않은 규칙이나 구현체를 그대로 적용하지 않는다.
  KMP·iOS 구조, Ramap 전용 타입·패키지·라이브러리와 작업 승인 절차는 가져오지 않는다.

## 승인된 모듈 구조

| 모듈 | 책임 | 현재 상태 |
| --- | --- | --- |
| `:app` | 앱 진입점, 전체 내비게이션, 의존성 조립 | 구현됨 |
| `:feature:attendance` | 출석 화면, ViewModel, UiState와 기능 흐름 | Android Library·Compose 기본 설정됨 |
| `:domain` | Kotlin 모델, 비즈니스 정책, Repository 인터페이스와 UseCase | 순수 Kotlin/JVM 기본 설정됨 |
| `:data` | Repository 구현, 데이터 소스, DTO와 매핑 | Android Library 기본 설정됨 |
| `:core:designsystem` | YAPP 테마와 재사용 가능한 Compose 컴포넌트 | 구현됨 |
| `:core:network` | HTTP 클라이언트와 공통 통신 설정 | Android Library 기본 설정됨 |

사용자 요청에 따라 목표 모듈 6개의 Gradle 설정과 의존성을 준비했다. 새 모듈의 업무 코드와 Network의 HTTP 라이브러리는 기능·API 요구사항이 정해질 때 구현·선택한다.

인증 기능이 확정되면 `:feature:auth`, 로컬 저장이 필요하면 `:core:datastore`를 추가한다. 추가 모듈은 실제 책임이 생길 때 만들고, 변경 시 위 상태와 README를 갱신한다. Domain과 Data의 기능별 분리는 규모와 의존 관계에 따라 결정한다.

### 의존성 방향

```text
:app → :feature:* / :data / :core:designsystem
:feature:* → :domain / :core:designsystem
:data → :domain / :core:network / 필요 시 :core:datastore
:domain → Kotlin 및 필요한 순수 Kotlin 라이브러리
```

- Feature는 Data 구현에 직접 의존하지 않는다. App의 의존성 조립에서 Domain 인터페이스와 Data 구현을 연결한다.
- Domain은 Android, Compose, HTTP 클라이언트, DB 구현에 의존하지 않는다.
- Feature끼리 직접 의존하지 않는다. 화면 간 이동은 App에서 조율하고 필요한 식별자와 입력만 전달한다.
- Designsystem은 App, Feature, Domain, Data에 의존하지 않는다. 테마와 공통 UI만 소유한다.
- Network와 Datastore는 Feature의 화면·상태를 알지 못한다. 순환 의존성을 만들지 않는다.

### 화면·데이터 구현 규칙

- 흐름은 `사용자 입력 → ViewModel → Repository/UseCase → UiState → Compose`다.
- 화면은 UiState를 표시하고 사용자 입력을 전달한다. 네트워크·DB·Repository 구현을 직접 호출하지 않는다.
- ViewModel은 읽기 전용 `StateFlow<UiState>`를 노출하고 UI는 생명주기에 맞춰 수집한다. Activity나 Context를 ViewModel에 보관하지 않는다.
- 로딩·실패·재시도 상태를 명시한다. 처리 결과를 UI 상태에 반영하고, 중요한 결과를 유실 가능한 일회성 이벤트로만 전달하지 않는다.
- UseCase는 실제 비즈니스 규칙, 여러 Repository의 조합, 재사용이 필요할 때 추가한다. 단순 전달용 클래스를 일괄 생성하지 않는다.
- DTO는 Data 내부에 두고 Domain 모델로 변환한다. 화면에 필요한 표현은 Feature의 UiState에서 정한다.
- 출석 정책과 API 계약은 확인된 요구사항을 기준으로 구현한다. 클라이언트 표시와 서버가 확정한 출석 결과를 구분한다.
- 비동기 작업은 Coroutines와 Flow를 사용하고 취소를 오류로 삼키지 않는다.

## 작업 오케스트레이션

다음 순서로 작업을 조율한다. 각 단계의 결과는 이슈·PR에 남겨 다른 담당자가 이어서 작업할 수 있게 한다.

| 단계 | 수행할 작업 | 다음 단계 조건 |
| --- | --- | --- |
| 1. 확인 | 요청, 관련 이슈, 현재 브랜치·diff, 실제 모듈과 기존 구현을 읽는다 | 변경 목적과 완료 기준을 설명할 수 있음 |
| 2. 계획 | 수정할 파일·모듈, 영역 라벨, 계약, 의존 순서와 PR 분리 계획을 정한다 | PR마다 한 개발 영역과 800줄 이하 범위가 정해짐 |
| 3. 이슈·브랜치 | 중복 이슈·PR을 검색하고 기존 이슈를 연결하거나 템플릿에 맞춰 생성한다. 최신 기준 브랜치에서 작업 브랜치를 만든다 | 작업 범위와 이슈 번호가 연결됨 |
| 4. 구현 | 계약과 모듈 경계를 지키며 범위 안의 변경을 구현한다 | 해당 PR이 독립적으로 빌드·검증 가능함 |
| 5. 검증·리뷰 | 필요한 검증을 실행하고 diff의 동작, 경계와 변경량을 확인한다 | 실패 해결 또는 미실행 사유와 영향 기록 |
| 6. 커밋·PR | 관련 변경만 커밋·푸시하고 PR 템플릿으로 작성한다. 생성 결과와 필수 검사를 확인한다 | PR 링크와 실제 검증 결과를 보고할 수 있음 |

기능 개발은 Domain 계약을 먼저 정의하고 Data 구현과 가짜 상태 기반 UI를 진행한 뒤 Feature에서 연결한다. 독립적인 PR은 `develop`을 대상으로 작성한다. 의존하는 후속 PR은 선행 PR 병합 후 최신 `develop`에서 진행한다. 여러 커밋으로 나누는 것만으로 PR 크기나 영역이 분리되지는 않는다.

변경 중 다른 담당자의 수정이 보이면 보존하고 반영한다. 자동으로 되돌리거나 덮어쓰지 않는다. 서브에이전트는 사용자가 요청한 경우에 사용한다. 사용 시 담당 파일·모듈, 입력 계약, 검증과 완료 기준을 지정하고 다른 담당자의 변경을 보존하도록 전달한다. 결과 통합과 전체 검증은 조율 담당자가 책임진다.

## GitHub 자동화와 PR 분리

- `main`과 `develop`에 직접 커밋·푸시하지 않는다. 일반 작업은 최신 `develop`에서 `feature/<이슈>-<설명>`, `fix/<이슈>-<설명>`, `chore/<이슈>-<설명>` 브랜치를 만든다.
- 개발 PR에는 `ui/ux`, `data`, `domain`, `feat` 중 정확히 하나를 붙인다. PR 라벨과 모듈은 일대일 관계가 아니다.
- `ui/ux`: 화면·공통 컴포넌트·디자인시스템. `data`: 데이터 소스·저장소 구현·DTO 매핑. `domain`: 정책·모델·인터페이스. `feat`: 상태 관리와 기능 연결.
- `feat`를 여러 개발 영역을 합치는 용도로 쓰지 않는다. 빌드·설정·문서는 `build`, 동작 변경 없는 구조 개선은 `refactor`, 버그 수정은 `bug`를 추가한다.
- 라벨은 GitHub에서 실제 존재하는지 확인한다. 삭제된 `chore`, `enhancement`, `documentation`, `priority:*` 라벨을 재생성하지 않는다.
- 모든 PR은 추가 줄 수 + 삭제 줄 수가 800 이하여야 한다. 로컬에서는 기준 브랜치와 merge-base의 diff로 확인하고, 생성 후에는 GitHub가 집계한 값을 확인한다. 파일 제외나 Draft로 제한을 우회하지 않는다.
- PR 제목은 한국어로 쓰고 `chore:`, `feat:`, `fix:` 같은 유형 접두어를 붙이지 않는다. 커밋 메시지에는 유형 접두어를 사용할 수 있으며 본문에 `Refs #번호`로 연결한다.
- PR 본문은 `.github/pull_request_template.md`를 따르고 목적, 영역, 변경량, 관련 이슈, 선행·후속 PR과 실제 검증 결과를 적는다. `develop` 대상은 `Related #번호`로 연결한다.
- `.codex/prompts/yapp-commit.md`, `yapp-issue.md`, `yapp-pr.md`의 확인 절차를 재사용한다. 슬래시 커맨드가 없는 환경에서는 동일한 절차를 Git과 `gh`로 수행한다.
- 같은 작업 브랜치의 열린 PR이 있으면 중복 생성하지 않고 갱신한다. 본문은 UTF-8 파일과 `--body-file`로 전달한다. 생성한 PR은 지원되는 경우 현재 Codex 작업에 첨부한다.
- `main`·`develop`의 `PR policy` 필수 검사를 확인한다. 대기 중인 검사를 통과했다고 보고하지 않는다. 병합은 사용자가 요청한 범위에서 저장소의 리뷰·검사 조건을 지켜 수행한다.

## 검증 명령

Android 변경의 기본 검증:

```bash
./gradlew :app:assembleDebug :app:lintDebug
```

Designsystem 변경은 라이브러리 자체와 App 소비를 함께 확인한다:

```bash
./gradlew :core:designsystem:assembleDebug :core:designsystem:lintDebug :app:assembleDebug :app:lintDebug
```

모듈 설정·의존성 변경은 전체 연결을 확인한다:

```bash
./gradlew :domain:build assembleDebug lintDebug
```

정책 검사나 자동화 스크립트를 바꾸면 관련 형식을 확인하고 정책 테스트를 실행한다:

```bash
python3 -m unittest discover -s scripts/tests -v
git diff --check
```

- 비즈니스 규칙, DTO 매핑, ViewModel의 상태 변화에는 의미 있는 테스트를 추가한다. 테스트 대역은 가능한 경우 Fake를 사용한다.
- UI 동작·화면을 바꾸면 적절한 Preview·기기·UI 테스트로 확인한다. 테마 이동처럼 표현이 그대로인 변경은 앱과 라이브러리 빌드·lint로 연결을 확인한다.
- 문서만 바꾸면 링크·경로·내용과 diff를 확인한다. 수행하지 않은 빌드·테스트나 존재하지 않는 모듈의 명령을 실행했다고 적지 않는다.
- 검증 실패는 원인을 해결하고 관련 검증만 다시 실행한다. 실행 환경 때문에 확인하지 못한 항목은 이유와 남은 영향을 기록한다.

## Code Review Rules

- 변경에 해당하는 프로젝트 스킬을 읽고 승인된 코딩·UI·데이터·모듈 규칙을 확인한다.
- 승인된 모듈 의존성 방향, DTO 경계, Compose와 ViewModel의 역할이 지켜지는지 확인한다.
- 로딩·실패·재시도, 중복 입력, 취소 처리와 요구사항의 주요 경계 조건을 확인한다.
- PR diff가 라벨의 작업 영역 하나에 해당하고 선행 계약과 호환되는지 확인한다. 실제 영역 분리는 라벨 검사만으로 증명되지 않는다.
- 완료 보고에는 변경 내용, 이슈·PR 링크, 변경량, 수행한 검증, 미검증 항목과 선행·후속 작업을 포함한다.

## SkillOpt-Sleep

Use the `skillopt-sleep` skill when I ask for a sleep, dream, offline self-improvement, or review of past Codex sessions. The runner is:
`bash "$HOME/.local/share/SkillOpt/plugins/run-sleep.sh" status --project "$(pwd)"`.
