---
authority: canonical
status: active
last_verified: 2026-10-30
---

# 디자인시스템 공통 컴포넌트

공통 Compose 컴포넌트는 `:core:designsystem`의 `components/` 패키지에 둡니다. 자세한 배치 규칙은 [모듈 구조](module-structure.md)를 따릅니다.

## YappText

공통 Compose 텍스트는 `:core:designsystem`의 `components/text/YappText.kt`에서 제공합니다. `YappText`는 Material 3 `Text`를 감싸며 `TextStyle`을 그대로 받습니다. 디자인 토큰이나 화면별 스타일을 `style`로 전달할 수 있습니다.

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

## 메인 헤더

메인 탭에서 재사용하는 제목·선택 아이콘 표현은 `components/main/YappMainHeader.kt`에서 제공합니다. 제목, 선택 아이콘, leading 슬롯과 클릭 콜백을 받아 표시하며 App이나 Feature의 내비게이션 상태를 소유하지 않습니다. 개별 Preview는 `src/debug`에 둡니다.

## 메인 아이콘 라벨

`components/main/YappIconLabel.kt`의 `YappIconLabel`은 공통 아이콘과 한 줄 텍스트를 가로로 표시합니다. 아이콘 리소스와 텍스트를 받고 선택적으로 tint를 적용합니다. 아이콘 크기·간격·텍스트 스타일은 디자인시스템 토큰으로 고정하며, 화면별 문구와 아이콘 선택은 호출부가 소유합니다. 홈 세션과 일정 항목에서 함께 사용합니다.

## Chip·Dropdown 표현 인자

`YappChip`의 `color`·`size`·`style`과 `YappDropdown`의 `placeholder`·`size`는 호출부에서 명시합니다. 기존 Preview는 이전 기본값을 직접 전달해 같은 표현을 유지합니다. `Modifier`와 Dropdown의 `enabled` 기본값은 유지합니다.

## 로그인·회원가입용 변형

[Figma 로그인·회원가입](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487)에서 확인한 변형만 명시적으로 선택합니다. 기존 기본값과 호출부 표현은 유지합니다.

- `YappButtonVariant.SolidBrand`와 `YappButtonSize.CallToAction`은 `#FF6600`, 18sp/24sp, 12dp 모서리, 가운데 정렬 CTA입니다. 비활성 상태는 버튼 전체를 50% 투명도로 표시하며 입력을 받지 않습니다.
- `YappButtonVariant.Kakao`와 `YappButtonSize.Social`은 높이 최소 54dp, 16sp/24sp, 12dp 모서리입니다. Feature가 제공자 로고를 36dp `leadingIcon` 슬롯에 전달하며 컴포넌트는 실제 인증을 수행하지 않습니다. 현재 로그인 화면은 Figma의 Google 버튼에 맞춰 `OutlinedAssistive`와 `Social` 변형을 사용하고, 18dp Google 로고를 슬롯 가운데 표시합니다.
- `YappTextFieldVariant.Auth`는 기존 `size` 대신 54dp 높이와 12dp 모서리를 사용합니다. `title`을 생략하면 라벨과 라벨 간격이 없습니다. 실제 포커스를 반영하되 호출부의 오류·성공 상태를 덮어쓰지 않습니다. 입력값과 검증은 호출부가 소유하며 `keyboardOptions`·`keyboardActions`로 IME를 연결합니다.
- `YappAlertDialogVariant.Compact`는 기본 최대 너비 285dp(호출부의 `compactWidth`로 조정), 16dp 모서리, 왼쪽 정렬 제목·본문, 44dp 높이와 8dp 모서리 버튼, 45% 배경 dim을 사용합니다. 닫기·제보 같은 행동은 호출부 콜백에 전달합니다.

표현 상태는 `src/debug`의 `AuthComponentsPreview.kt`에서 확인합니다. 화면·상태 전이와 제공자 로고 리소스는 각 Feature가 소유합니다.

## 인증 공통 표현

`components/auth/`의 `AuthLayout`, `AuthHeading`, `AuthGraphic`, `AuthPrimaryAction`, `AuthErrorDialog`는 표시값과 콜백으로 인증 UI 표현을 재사용합니다. `AuthLayout`은 뒤로 버튼과 content·bottom 슬롯을 제공하고 시스템 뒤로 가기·inset은 App에서 공급합니다. 오류 문구와 닫기·제보 행동도 호출부에서 전달합니다.

각 컴포넌트는 `src/debug`의 `components/auth/AuthPreviews.kt`에 개별 Preview를 갖습니다. 화면·목적지·검증 규칙·인증 연동은 이 공통 표현의 책임에 포함하지 않습니다.

## 홈 Figma 변형

- 홈 빠른 메뉴의 전체 세션·출결 보고 카드는 높이 70dp와 12dp 모서리를 사용합니다. 출결 보고의 내보낸 일러스트는 카드에 보이는 102×70dp 영역을 이미 포함하므로 확대하거나 추가 이동하지 않고 오른쪽 위에 배치합니다. 제출 콜백 연결은 Feature에서 담당합니다.

- `YappButtonSize.HomePrimary`는 52dp·16sp/22sp·12dp 모서리, `HomeSecondary`는 44dp·14sp/19sp·8dp 모서리입니다. `SolidNeutral`은 홈의 검정 출석 버튼과 비활성 회색 표현을 제공합니다.
- `YappBottomSheetVariant.Home`은 가로 바깥 여백 10dp, 모서리 16dp, 52×4dp 핸들을 포함한 32dp 상단 영역, 본문 가로/하단 여백 18dp와 46% dim을 제공합니다. `isDismissible=false`이면 제출 중 숨김 전이를 막습니다. 기존 기본 변형은 유지합니다.
- `YappToastVariant.Home`은 가로 전체 너비, 46dp 최소 높이, 8dp 모서리와 확인 액션을 제공합니다. 메시지·닫기 상태는 Feature에서 전달합니다.
- `YappTextFieldVariant.Home`은 보고 사유 입력의 50dp 높이·12dp 모서리를 제공하며 `textStyle`로 홈 서체를 전달합니다. `YappMainHeader.iconTint`는 홈의 알림 색상을 선택하며 기본 호출부 표현은 유지합니다.
- 새로운 변형의 개별 Preview는 `src/debug`의 `components/HomeControlsPreviews.kt`에 둡니다.

## My Figma 변형

- `YappChipSize.Profile`은 13sp/18sp 글자, 가로 10dp·세로 2dp 여백과 8dp 모서리를 사용합니다. `YappChipColor.Brand`와 함께 프로필의 활동회원 배지를 표시합니다. 개별 Preview는 `src/debug`의 `components/chip/ProfileChipPreview.kt`에서 확인합니다.
- `YappMainColor.basement`, `layerFill`, `critical`, `criticalWeak`는 My 요약 카드 배경과 결석 배지에 사용하는 Figma 색상입니다.
- My·설정의 API 연결 범위와 표시 정책은 [DEC-0043](decisions.md#dec-0043--my-출석-조회-연결과-figma-ui-기준)을 따릅니다.
