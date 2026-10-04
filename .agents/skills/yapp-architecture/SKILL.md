---
name: yapp-architecture
description: YAPP 출석 Android 앱에서 책임을 배치하거나 계층 경계를 변경할 때 승인된 MVVM, UDF, Clean Architecture와 의존성 방향을 적용한다.
---

# YAPP Architecture

계층 책임과 의존성의 기준은 [Wiki 아키텍처](../../../docs/wiki/architecture.md)다. 모듈 상태와 파일 배치는 [Wiki 모듈 구조](../../../docs/wiki/module-structure.md)를 함께 읽는다.

변경 전 호출부, Gradle 의존성, 객체 조립 위치와 관련 테스트를 확인한다. 변경 후 계약 호환성·의존성 방향을 검토하고 영향받는 모듈부터 검증한다. 정책의 의미가 바뀌면 [Wiki 갱신 절차](../../../docs/wiki/README.md)에 따라 결정 기록도 갱신한다.
