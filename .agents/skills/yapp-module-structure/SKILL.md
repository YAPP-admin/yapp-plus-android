---
name: yapp-module-structure
description: YAPP Android 프로젝트에서 파일 위치·패키지를 정하거나 Gradle 모듈을 추가·이동할 때 현재 모듈과 승인된 목표 구조, 등록·의존성·검증 기준을 적용한다.
---

# YAPP Module Structure

실제 등록 여부는 [settings.gradle.kts](../../../settings.gradle.kts),
승인된 책임·상태는 [AGENTS.md](../../../AGENTS.md)를 확인한다.
아키텍처 경계는 [yapp-architecture](../yapp-architecture/SKILL.md)를 따른다.

## 현재 모듈과 목표 구조

| 모듈 | 패키지 기준 | 추가 시점 |
| --- | --- | --- |
| `:app` | `com.yapp.plus` | 현재 존재 |
| `:core:designsystem` | `com.yapp.plus.core.designsystem` | 현재 존재 |
| `:feature:attendance` | `com.yapp.plus.feature.attendance` | Android Library·Compose 기본 설정됨 |
| `:domain` | `com.yapp.plus.domain` | 순수 Kotlin/JVM 기본 설정됨 |
| `:data` | `com.yapp.plus.data` | Android Library 기본 설정됨 |
| `:core:network` | `com.yapp.plus.core.network` | Android Library 기본 설정됨 |
| `:feature:auth` | `com.yapp.plus.feature.auth` | 인증 기능 확정 시 |
| `:core:datastore` | `com.yapp.plus.core.datastore` | 로컬 저장 필요 시 |

사용자 요청에 따라 목표 모듈의 기본 설정을 준비했다. 추가 모듈은 실제 책임과 사용처가 생길 때 만든다.
Domain·Data의 기능별 분리는 규모와 의존 관계에 따라 결정한다.

## 파일 배치

- Android 모듈의 Kotlin 소스는 `src/main/java/<패키지 경로>/`에 둔다.
- 순수 Kotlin/JVM Domain 소스는 `domain/src/main/kotlin/<패키지 경로>/`에 둔다.
- 단위 테스트는 `src/test/`, 기기 테스트는 `src/androidTest/`의 해당 패키지에 둔다.
- App은 Android 진입점·전체 내비게이션·객체 조립을 소유한다.
- Feature 안에서 화면·ViewModel·UiState와 해당 기능의 UI 변환을 함께 관리한다.
- Domain에는 모델·정책·Repository 계약·필요한 UseCase를 둔다.
- Data에는 Repository 구현·DataSource·DTO·Domain 변환을 둔다.
- Designsystem의 기존 테마는 `core/designsystem/src/main/java/` 아래
  `com/yapp/plus/core/designsystem/theme/`에 있다. 공통 컴포넌트도 같은 모듈에서 관리한다.
- 사용자 문구는 해당 Android 모듈의 `src/main/res/values/strings.xml`에 둔다.
- 범용 파일·모듈을 미리 만들지 않는다. 여러 사용처의 실제 공통 책임이 확인되면 공통화한다.

## 의존성과 테스트 도구

- App → Feature·Data·Designsystem, Feature → Domain·Designsystem 방향을 지킨다.
- Data → Domain·Network·필요한 Datastore 방향을 지킨다.
- Domain은 필요한 순수 Kotlin 라이브러리만 사용한다.
- Feature끼리 직접 의존하거나 순환 의존성을 만들지 않는다.
- 여러 모듈에서 재사용할 Fake·테스트 도구는 필요할 때 공통화한다.
  공통 테스트 도구는 `testImplementation`·`androidTestImplementation` 등 테스트 의존성으로 연결한다.

## 모듈 추가·이동

1. 실제 책임·사용처·계약과 호출부를 확인한다.
2. 파일·패키지를 배치하고 모듈 추가 시 `settings.gradle.kts`에 등록한다.
3. 현재 Gradle 설정·버전 카탈로그를 활용하고 승인된 의존성 방향을 확인한다.
4. 각 모듈의 객체 구성 정보와 App의 조립을 함께 확인한다.
5. 영향받는 모듈과 호출부를 우선 검증하고 AGENTS.md·README의 실제 상태를 갱신한다.

아직 없는 모듈이나 검사 도구의 명령을 실행했다고 보고하지 않는다.
