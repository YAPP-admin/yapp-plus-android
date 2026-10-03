# YAPP 출석 Android

[YAPP](https://www.yapp.co.kr/)의 공식 출석 앱 Android 프로젝트입니다.

## 현재 상태

Jetpack Compose 기반의 단일 `:app` 모듈과 시작 화면이 준비되어 있습니다. 출석 기능, 인증, 서버 연동은 아직 구현되지 않았습니다.

## 개발 환경

- Android Studio와 Android SDK 37
- JDK 25 (Gradle daemon JVM 설정으로 자동 선택)
- Kotlin 2.2.10, Android Gradle Plugin 9.5.0-alpha02, Gradle 9.6.0
- minSdk 24, targetSdk 37

Android Studio에서 프로젝트 루트를 열고 Gradle 동기화를 실행합니다. 로컬 SDK 경로는 `local.properties`에 설정하며 이 파일은 Git에서 제외됩니다.

## 빌드

```bash
./gradlew :app:assembleDebug
```

단위 테스트가 추가되면 다음 명령으로 실행합니다.

```bash
./gradlew :app:testDebugUnitTest
```

현재 `applicationId`와 네임스페이스는 `com.yapp.plus`입니다. 배포용 앱 ID는 Play Console 등록 전에 확정해야 합니다.

## 기여

브랜치 생성, PR 대상, 릴리즈 절차는 [기여 및 Git Flow 규칙](CONTRIBUTING.md)을 따릅니다.
