---
type: knowledge-candidate
authority: none
status: promoted
created_at: 2026-11-24
updated_at: 2026-11-24
use_count: 1
last_used_at: 2026-11-24
target_path: docs/wiki/design-patterns.md
resolution_reason: 사용자가 BaseViewModel의 위치를 :core:ui로 정하고 두 일정 조회에 설계를 적용하도록 승인했다.
---

# YappResult와 BaseViewModel

홈과 일정 탭이 같은 Repository에서 받아 처리하던 기수 부재와 일반 실패를 Data에서 한 번 해석한다. 각 화면은 요청 진행 상태와 결과를 표시하는 방법을 결정한다. [승인된 설계 패턴](../design-patterns.md#홈과-일정-탭의-일정-조회)

- Domain의 `YappResult<T, E>`는 성공 값과 기능별 실패 사유를 표현한다. [결과 타입](../../../domain/src/main/kotlin/com/yapp/plus/domain/result/YappResult.kt) · [일정 Repository 구현](../../../data/src/main/java/com/yapp/plus/data/session/SessionScheduleRepositoryImpl.kt)
- `:core:ui`의 `BaseViewModel<S>`는 상태를 공개하고 요청 결과를 최신 상태에 반영한다. 요청이 진행되는 동안 `scheduleJob?.isActive == true`가 되어 같은 일정에 대한 재시도를 건너뛴다. [BaseViewModel](../../../core/ui/src/main/java/com/yapp/plus/core/ui/BaseViewModel.kt) · [일정 탭 사용 예시](../../../feature/main/schedule/src/main/java/com/yapp/plus/feature/main/schedule/ScheduleViewModel.kt) · [홈 사용 예시](../../../feature/main/home/src/main/java/com/yapp/plus/feature/main/home/HomeViewModel.kt)

## 실제 사용 이력

| 작업 ID 또는 이슈 | 사용일 | 맥락 | 실제 판단 근거 |
| --- | --- | --- | --- |
| [Android #234](https://github.com/YAPP-admin/yapp-plus-android/issues/234) | 2026-11-24 | implementation | 일정 Repository의 실패 계약과 두 ViewModel의 중복 조회·취소·상태 갱신 경계를 정했다. |

## 처리

- 결정 ID: DEC-0061
