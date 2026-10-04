---
authority: canonical
status: active
last_verified: 2026-10-04
---

# YAPP LLM Wiki

YAPP 출석 Android 앱의 정책과 설계 결정을 찾고 유지하는 문서입니다. 현재 적용되는 규칙은 분야별 문서에, 변경 이유와 이전 결정은 [결정 기록](decisions.md)에 둡니다. 코드와 문서의 실제 상태가 다르면 변경 작업에서 함께 바로잡습니다.

| 분야 | 기준 문서 |
| --- | --- |
| 앱의 출석 정책과 미확정 요구사항 | [제품 정책](product-policy.md) |
| 계층 책임과 의존성 | [아키텍처](architecture.md) |
| 모듈 상태와 파일 배치 | [모듈 구조](module-structure.md) |
| 화면·상태·데이터 접근 패턴 | [설계 패턴](design-patterns.md) |
| Kotlin·Gradle Kotlin 작성 규칙 | [코딩 컨벤션](coding-conventions.md) |
| 정책 변경의 근거와 이력 | [결정 기록](decisions.md) |
| 미확정 지식과 승격 절차 | [지식 후보함](inbox/README.md) |
| 브랜치·PR·라벨·변경량 | [CONTRIBUTING.md](../../CONTRIBUTING.md) |
| 기계가 읽는 서식 설정 | [.editorconfig](../../.editorconfig) |

[AGENTS.md](../../AGENTS.md)는 에이전트 작업 절차, `.agents/skills/`는 작업별 적용 안내입니다. 이 두 곳과 README는 Wiki 규칙을 반복 정의하지 않고 연결합니다. `settings.gradle.kts`와 소스 코드는 구현 상태를 확인하는 근거입니다. 문서와 구현이 어긋나면 확정된 정책과 실제 코드를 확인해 같은 변경에서 정리합니다.

## 세션에서 정책을 갱신하는 방법

Codex는 이 저장소에서 작업할 때 아래 절차를 수행합니다. 대화 밖에서 실행되는 자동 수집기가 아니며, 세션에서 합의한 내용을 작업 문서에 반영하는 절차입니다.

1. 사용자에게서 새 정책, 변경, 폐기가 **명확히 확정**되었는지 확인합니다. 제안·질문·예시·다른 프로젝트의 규칙은 확정 정책으로 기록하지 않습니다. 확정 전 재사용 지식은 [후보함](inbox/README.md)에 기록할 수 있습니다. 결정에 필요한 제품 정책이나 API 계약이 빠졌다면 해당 항목을 [미확정 목록](product-policy.md)에 두고 필요한 정보만 묻습니다.
2. 기존 Wiki 규칙과 [결정 기록](decisions.md)을 검색합니다. 같은 규칙이 있으면 새 문서를 늘리기보다 해당 분야 문서를 고칩니다. 정책이 적용되는 모듈, API, 화면, 문서의 영향을 확인합니다.
3. 현재 규칙을 담은 분야별 문서를 갱신하고, 결정 기록에 날짜(Asia/Seoul), 결정·이유·근거, 바뀐 문서, 대체한 결정 ID를 남깁니다. 이전 결정은 지우지 않습니다. 이슈가 있으면 연결하고, 세션 합의만 근거라면 그렇게 표시합니다. 민감 정보와 개인 대화 원문은 옮기지 않습니다.
4. 구현이 필요한 결정은 코드·검증과 함께 처리합니다. 구현 전 결정이라면 문서에 적용 상태를 분명히 적습니다. 모듈 상태가 바뀌면 [모듈 구조](module-structure.md), `AGENTS.md`, `README.md`도 맞춥니다.
5. PR을 만들기 전에 `python3 scripts/check_wiki.py`로 형식·링크·후보 상태를 확인하고 규칙과 기록의 의미가 일치하는지 검토합니다. PR 본문에 정책 문서와 결정 ID를 적습니다. 정책 결정이 없었던 작업은 기록을 억지로 추가하지 않습니다.

기존 승인 규칙은 [#3](https://github.com/YAPP-admin/yapp-plus-android/issues/3)과 [PR #4](https://github.com/YAPP-admin/yapp-plus-android/pull/4)를 기준으로 옮겼습니다. 새 결정은 해당 분야 문서와 기록을 같은 PR에서 변경합니다.
