---
authority: canonical
status: active
last_verified: 2026-10-07
---

# 디자인시스템 공통 컴포넌트

## YappText

공통 Compose 텍스트는 `:core:designsystem`의 `component/text/YappText.kt`에서 제공합니다. `YappText`는 Material 3 `Text`를 감싸며 `TextStyle`을 그대로 받습니다. 디자인 토큰이나 화면별 스타일을 `style`로 전달할 수 있습니다.

호출부에서 `style`과 `color`를 명시합니다. 정렬, 장식, 최소·최대 줄 수, 줄바꿈, overflow, 레이아웃 콜백을 지원합니다. 시스템 글꼴 크기 설정을 적용하기 위해 `LocalDensity`를 변경하지 않습니다.

```kotlin
YappText(
    text = "출석 정보를 확인해 주세요.",
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.onSurface,
    modifier = Modifier.padding(horizontal = 16.dp),
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
)
```

컴포넌트 사용 범위는 공통으로 관리할 텍스트 표현입니다. 기능 화면의 상태·동작 로직은 각 Feature 모듈에 둡니다.

## Chip·Dropdown 표현 인자

`YappChip`의 `color`·`size`·`style`과 `YappDropdown`의 `placeholder`·`size`는 호출부에서 명시합니다. 기존 Preview는 이전 기본값을 직접 전달해 같은 표현을 유지합니다. `Modifier`와 Dropdown의 `enabled` 기본값은 유지합니다.

## 로그인·회원가입용 변형

[Figma 로그인·회원가입](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487)에서 확인한 변형만 명시적으로 선택합니다. 기존 기본값과 호출부 표현은 유지합니다.

- `YappButtonVariant.SolidBrand`와 `YappButtonSize.CallToAction`은 `#FF6600`, 18sp/24sp, 12dp 모서리, 가운데 정렬 CTA입니다. 비활성 상태는 버튼 전체를 50% 투명도로 표시하며 입력을 받지 않습니다.
- `YappButtonVariant.Kakao`와 `YappButtonSize.Social`은 높이 최소 54dp, 16sp/24sp, 12dp 모서리입니다. Feature가 Figma 원본 카카오 로고를 `leadingIcon` 슬롯에 전달합니다. 슬롯은 36dp이며 컴포넌트는 실제 인증을 수행하지 않습니다.
- `YappTextFieldVariant.Auth`는 기존 `size` 대신 54dp 높이와 12dp 모서리를 사용합니다. `title`을 생략하면 라벨과 라벨 간격이 없습니다. 실제 포커스를 반영하되 호출부의 오류·성공 상태를 덮어쓰지 않습니다. 입력값과 검증은 호출부가 소유하며 `keyboardOptions`·`keyboardActions`로 IME를 연결합니다.
- `YappAlertDialogVariant.Compact`는 최대 너비 285dp, 12dp 모서리, 왼쪽 정렬 제목·본문, 44dp 높이와 8dp 모서리 버튼, 45% 배경 dim을 사용합니다. 닫기·제보 같은 행동은 호출부 콜백에 전달합니다.

표현 상태는 `src/debug`의 `AuthComponentsPreview.kt`에서 확인합니다. 화면·상태 전이와 카카오 로고 리소스는 후속 Feature 구현이 소유합니다.

## 인증 공통 표현

`component/auth/`의 `AuthLayout`, `AuthHeading`, `AuthGraphic`, `AuthPrimaryAction`, `AuthErrorDialog`는 표시값과 콜백으로 인증 UI 표현을 재사용합니다. `AuthLayout`은 뒤로 버튼과 content·bottom 슬롯을 제공하고 시스템 뒤로 가기·inset은 App에서 공급합니다. 오류 문구와 닫기·제보 행동도 호출부에서 전달합니다.

각 컴포넌트는 `src/debug`의 `component/auth/AuthPreviews.kt`에 개별 Preview를 갖습니다. 화면·목적지·검증 규칙·인증 연동은 이 공통 표현의 책임에 포함하지 않습니다.
