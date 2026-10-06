---
authority: canonical
status: active
last_verified: 2026-10-06
---

# 모듈 구조

실제 등록 여부는 [settings.gradle.kts](../../settings.gradle.kts)에서 확인합니다. 계층 경계는 [아키텍처](architecture.md)를 따릅니다.

| 모듈 | 패키지 기준 | 현재 상태 또는 추가 시점 |
| --- | --- | --- |
| `:app` | `com.yapp.plus` | 앱 진입점 구현됨 |
| `:core:designsystem` | `com.yapp.plus.core.designsystem` | 테마 구현됨 |
| `:feature:attendance` | `com.yapp.plus.feature.attendance` | Android Library·Compose 기본 설정됨 |
| `:domain` | `com.yapp.plus.domain` | 순수 Kotlin/JVM 기본 설정됨 |
| `:data` | `com.yapp.plus.data` | Android Library 기본 설정됨 |
| `:core:network` | `com.yapp.plus.core.network` | Android Library 기본 설정됨 |
| `build-logic` | Gradle convention plugins | 별도 included build; 앱 모듈이 아님 |
| `:feature:auth` | `com.yapp.plus.feature.auth` | 인증 기능 확정 시 추가 |
| `:core:datastore` | `com.yapp.plus.core.datastore` | 로컬 저장 필요 시 추가 |

6개 앱 모듈의 공통 Gradle 설정은 `build-logic` convention plugin으로 관리합니다. App은 Hilt 진입점을 갖고, `:core:network`는 Ktor Android client와 Kotlin serialization JSON 의존성을 사용합니다. 실제 API 호출과 DTO는 서버 계약이 정해진 뒤 구현합니다. Domain과 Data의 기능별 분리는 규모와 의존 관계에 따라 결정합니다.

## 파일 배치

- Android 모듈의 Kotlin 소스는 `src/main/java/<패키지 경로>/`에 둡니다. 순수 Kotlin/JVM Domain 소스는 `domain/src/main/kotlin/<패키지 경로>/`에 둡니다.
- 단위 테스트는 `src/test/`, 기기 테스트는 `src/androidTest/`의 해당 패키지에 둡니다.
- App에는 Android 진입점·전체 내비게이션·객체 조립을, Feature에는 화면·ViewModel·UiState와 기능별 UI 변환을 둡니다.
- Domain에는 모델·정책·Repository 계약·필요한 UseCase를, Data에는 Repository 구현·DataSource·DTO·Domain 변환을 둡니다.
- Designsystem의 테마는 `core/designsystem/src/main/java/com/yapp/plus/core/designsystem/theme/`에 있습니다. 공통 컴포넌트도 이 모듈에서 관리합니다.
- 사용자 문구는 해당 Android 모듈의 `src/main/res/values/strings.xml`에 둡니다.
- 범용 파일·모듈을 미리 만들지 않습니다. 여러 사용처의 실제 공통 책임이 확인되면 공통화합니다.

## 모듈 변경 절차

모듈을 추가하거나 옮길 때 실제 책임·사용처·계약·호출부를 확인합니다. 파일·패키지를 배치하고 `settings.gradle.kts`에 등록한 뒤 [아키텍처의 의존성 방향](architecture.md)과 App의 객체 조립을 확인합니다. 재사용할 Fake·테스트 도구는 필요할 때 테스트 의존성으로만 연결합니다. 영향받는 모듈과 호출부를 검증하고 이 문서, `AGENTS.md`, `README.md`의 상태를 함께 갱신합니다. 없는 모듈이나 검사 도구의 명령을 실행했다고 보고하지 않습니다.
