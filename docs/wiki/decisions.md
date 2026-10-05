---
authority: canonical
status: active
last_verified: 2026-10-06
---

# 결정 기록

확정된 정책의 변경 이유를 날짜순으로 남깁니다. 현재 적용 규칙은 분야별 문서에서 읽습니다. 새 결정에는 다음 `DEC-0004`부터 순서대로 ID를 부여하고, 이전 결정을 대체하면 해당 ID를 기록합니다. 이전 항목은 삭제하거나 현재 규칙처럼 수정하지 않습니다.

## DEC-0001 · 기존 승인 규칙을 Wiki의 초기 기준으로 이전

- 기록일: 2026-10-04 (기존 결정의 원래 확정일이 아닌 이전 작업일)
- 결정: 승인된 아키텍처, 모듈, 설계 패턴, 코딩 규칙을 분야별 Wiki 문서의 초기 기준으로 삼습니다.
- 이유: 에이전트 작업 지침과 실제 프로젝트 규칙을 분리하고 중복을 줄입니다.
- 근거: [이슈 #3](https://github.com/YAPP-admin/yapp-plus-android/issues/3), [PR #4](https://github.com/YAPP-admin/yapp-plus-android/pull/4), 기존 `AGENTS.md`와 프로젝트 스킬.
- 반영: [아키텍처](architecture.md), [모듈 구조](module-structure.md), [설계 패턴](design-patterns.md), [코딩 컨벤션](coding-conventions.md), [제품 정책](product-policy.md).
- 대체한 결정: 없음.

## DEC-0002 · 세션 정책 갱신과 지식 후보 승격 절차 도입

- 기록일: 2026-10-04
- 결정: 명확히 확정된 정책은 해당 Wiki와 이 기록에 같은 작업에서 반영합니다. 근거가 부족한 반복 지식은 inbox 후보로 보관하고 사용 근거를 검토한 뒤 승격합니다.
- 이유: 세션에서 생긴 장기 지식의 누락을 줄이고 후보를 공식 정책으로 오인하지 않도록 합니다.
- 근거: 이번 세션의 사용자 요청, [이슈 #5](https://github.com/YAPP-admin/yapp-plus-android/issues/5), [참고 저장소의 Wiki 유지보수 흐름](https://github.com/chanho0908/Keepiluv-Agent/blob/3e570985bf6561e59f419380f4d4ea8514c24ee7/wiki/schema/maintenance.md).
- 반영: [Wiki 운영](README.md), [후보함](inbox/README.md), `AGENTS.md`.
- 대체한 결정: 없음.

## DEC-0003 · Figma 타이포그래피를 앱 디자인시스템의 기준으로 적용

- 기록일: 2026-10-05
- 결정: YAPP Figma Typography 페이지의 25개 텍스트 스타일, Pretendard JP 글꼴과 `"ss10" 1` 글꼴 기능을 `:core:designsystem`의 기준으로 적용합니다. Material 3 타이포그래피 슬롯은 이 토큰으로 연결합니다.
- 이유: 화면에서 반복 가능한 글자 스타일을 앱 코드에서 공유하고, 디자인과 구현의 수치를 일치시킵니다.
- 근거: [이슈 #9](https://github.com/YAPP-admin/yapp-plus-android/issues/9), [YAPP Figma Typography](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-50739), 사용자 확인.
- 반영: [디자인시스템](design-system.md), `:core:designsystem`의 `YappTypography`와 `YappTheme`.
- 대체한 결정: 없음.

## DEC-0004 · 공통 컴포넌트 색상 토큰 중앙화

- 기록일: 2026-10-06
- 결정: 공통 디자인시스템 컴포넌트의 고정 색상은 `:core:designsystem/theme/Color.kt`의 `YappColor` 토큰으로 관리하고 컴포넌트와 Preview에서 재사용합니다. 기존 ARGB·알파값은 유지합니다.
- 이유: 동일 색상의 컴포넌트별 중복 선언을 없애고 디자인 변경을 한 곳에서 반영합니다.
- 근거: 이번 세션의 사용자 요청, [이슈 #13](https://github.com/YAPP-admin/yapp-plus-android/issues/13)의 Figma Components 구현 범위.
- 반영: [디자인시스템](design-system.md), `:core:designsystem`의 `theme/Color.kt`, PR #14·#16·#18·#19.
- 대체한 결정: 없음.
