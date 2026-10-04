---
name: yapp-module-structure
description: YAPP Android 프로젝트에서 파일 위치·패키지를 정하거나 Gradle 모듈을 추가·이동할 때 현재 모듈과 승인된 목표 구조, 등록·의존성·검증 기준을 적용한다.
---

# YAPP Module Structure

승인된 책임·상태와 파일 배치는 [Wiki 모듈 구조](../../../docs/wiki/module-structure.md), 실제 Gradle 등록은 [settings.gradle.kts](../../../settings.gradle.kts)를 확인한다. 계층 경계는 [Wiki 아키텍처](../../../docs/wiki/architecture.md)를 따른다.

모듈을 추가·이동할 때 사용처, 계약, 호출부, Gradle 의존성, App 객체 조립을 확인한다. 영향받는 모듈을 검증하고 Wiki·AGENTS.md·README의 실제 상태를 함께 갱신한다.
