---
authority: canonical
status: active
last_verified: 2026-10-05
---

# 디자인시스템 공통 컴포넌트

## YappText

공통 Compose 텍스트는 `:core:designsystem`의 `component/text/YappText.kt`에서 제공합니다. `YappText`는 Material 3 `Text`를 감싸며 `TextStyle`을 그대로 받습니다. 디자인 토큰이나 화면별 스타일을 `style`로 전달할 수 있습니다.

별도 값을 전달하지 않으면 현재 `LocalTextStyle`과 `LocalContentColor`를 따릅니다. 정렬, 장식, 최소·최대 줄 수, 줄바꿈, overflow, 레이아웃 콜백을 지원합니다. 시스템 글꼴 크기 설정을 적용하기 위해 `LocalDensity`를 변경하지 않습니다.

```kotlin
YappText(
    text = "출석 정보를 확인해 주세요.",
    style = MaterialTheme.typography.bodyLarge,
    modifier = Modifier.padding(horizontal = 16.dp),
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)
```

컴포넌트 사용 범위는 공통으로 관리할 텍스트 표현입니다. 기능 화면의 상태·동작 로직은 각 Feature 모듈에 둡니다.
