---
authority: canonical
status: active
last_verified: 2026-10-18
---

# 디자인시스템

## 색상

공용 Compose 색상 토큰은 `:core:designsystem`의 `theme/Color.kt`에 있는 `YappColor`에서 제공합니다. Figma Components 기준으로 이미 구현한 ARGB 값과 알파값을 보존하면서 컴포넌트와 Preview가 같은 토큰을 사용합니다. 컴포넌트 파일에 고정 색상 리터럴이나 로컬 색상 상수를 새로 두지 않습니다.

[로그인·회원가입 Figma](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487)의 변형은 별도 색상 토큰을 사용합니다. 기존 `primary`와 공용 컴포넌트 기본값을 바꾸지 않습니다.

Feature의 제목·설명·링크·배경에는 공개 `YappAuthColor`의 `brand`, `textPrimary`, `textSecondary`, `textSubtle`, `textFaint`, `background`를 사용합니다. 나머지 공용 색상과 컴포넌트 내부 색상은 `YappColor`에서 관리합니다.

메인 탭 데모의 배경·표면·본문·구분선 색상은 `:core:designsystem/theme/YappMainColor.kt`에서 관리합니다. 공통 헤더·데모 안내·아이콘과 텍스트 행은 `component/main/`의 `YappMainHeader`, `YappDemoDataLabel`, `YappIconLabel`로 제공합니다. 안내 문구와 화면 동작은 Feature가 소유합니다. 인증 화면에서 사용하는 기존 색상 토큰은 변경하지 않습니다.

| 토큰 | 값 | 사용처 |
| --- | --- | --- |
| `brand` | `#FF6600` | CTA·Compact 팝업의 주요 버튼 |
| `splashGradientEnd` | `#FF8500` | 스플래시 화면의 브랜드 세로 그라데이션 끝 색상 |
| `foregroundNeutral` | `#1A1C20` | Auth 입력·Compact 제목·카카오 버튼 글자 |
| `foregroundMuted` | `#555D6D` | Auth 포커스 테두리·커서·Compact 본문 |
| `foregroundSubtle` | `#868B94` | Compact 보조 버튼 글자 |
| `foregroundFaint` | `#B0B3BA` | Auth placeholder |
| `strokeNeutralWeak` | `#DCDEE3` | Auth 기본 테두리 |
| `backgroundBasement` | `#F3F4F5` | Compact 보조 버튼 배경 |
| `kakaoContainer` | `#FAE300` | 카카오 버튼 배경 |

## 아이콘

[YAPP Figma 아이콘 모음](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-57816)의 탭 아이콘 4개와 Lucide 영역의 아이콘 17개를 `:core:designsystem/src/main/res/drawable/`에 제공합니다. 모두 원본 경로·색상·선 두께를 보존한 24dp VectorDrawable이며, Feature는 공통 디자인시스템의 `R.drawable`을 참조합니다.

| Figma 아이콘 | 리소스 |
| --- | --- |
| house, calendar, presentation, user | `ic_navigation_house`, `ic_navigation_calendar`, `ic_navigation_presentation`, `ic_navigation_user` |
| chevron | `ic_chevron_left`, `ic_chevron_right`, `ic_chevron_up`, `ic_chevron_down` |
| brand logo | `ic_apple`, `ic_kakao_chat` |
| bell | `ic_notification`, `ic_notification_badge` |
| menu | `ic_menu` |
| check, notice, info | `ic_status_success`, `ic_status_warning`, `ic_status_info` |
| setting | `ic_settings` |
| question | `ic_help` |
| clock | `ic_clock` |
| map-pin | `ic_map_pin` |
| link | `ic_link` |

단색 탭·방향 아이콘은 화면 상태에 맞는 색상으로 tint할 수 있습니다. 원본의 흰색 기호·빨간 알림 점·회색 배경을 포함한 `ic_status_*`, `ic_notification_badge`, `ic_help`는 Compose `Icon`에 `tint = Color.Unspecified`를 지정하거나 `Image`로 표시해 리소스 색상을 보존합니다. 브랜드 로고도 원본 색상을 보존해 사용합니다.

## YAPP 로고

홈의 `graphic_yapp_logo`는 [Figma YAPP-logo](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=142-23072) 자체를 SVG로 내보내 변환한 `res/drawable/graphic_yapp_logo.xml`입니다. 원본의 80×30 영역, 내부 여백, 네 개의 경로, `#1A1C20` 색상과 투명 배경을 보존합니다. 홈은 기존 `R.drawable.graphic_yapp_logo`와 80dp×30dp 크기로 표시합니다.

스플래시 화면의 흰색 Y 심볼은 [Figma splash](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=418-14582)의 벡터 경로를 `:app`의 `drawable/ic_yapp_splash_symbol.xml`로 변환해 사용합니다. 리소스의 50dp×70dp 영역과 원본 경로의 내부 여백을 유지합니다.

## 그래픽과 UI 에셋

[Figma Graphic 페이지](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-26431)의 Hero 8종, Point 6종, None 1종은 Figma 벡터 레이어를 Android VectorDrawable `res/drawable/graphic_*.xml`로 변환해 제공합니다. 드롭다운 방향 표시 2종과 인증 화면 뒤로가기 아이콘도 [Figma Components](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-17002) 및 [Icons](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-57816)의 벡터 레이어를 사용합니다. Compose에서는 `painterResource(R.drawable.*)`로 표시하고 컴포넌트가 기존 표시 크기를 지정합니다.

Figma의 알파 마스크는 VectorDrawable의 클리핑 경로로 옮겼습니다. 원본의 `mix-blend-mode:multiply`는 VectorDrawable에 대응 속성이 없어 생략했으며, 겹친 음영 일부는 달라질 수 있습니다. Figma SVG에 잘못된 `NaN` 좌표가 있던 경로는 그 점을 제외하고 앞뒤의 유효 좌표를 연결했습니다. `ic_input_required.xml`은 같은 모양의 Figma 레이어를 찾지 못해 기존 16dp PNG의 중앙 4dp 점을 벡터로 다시 그린 에셋이며, 기존 크기와 색을 유지합니다.

## 하단 내비게이션

`:core:designsystem/component/navigation/`의 `YappBottomNavigation`은 탭 배치·상단 구분선을, `YappBottomNavigationItem`은 아이콘·라벨·선택 상태와 탭 semantics를 담당합니다. App이 아이콘 리소스, 표시 라벨, 선택 상태와 클릭 콜백을 전달하고 탭 선택 상태와 화면 연결도 App에서 소유합니다. 공통 컴포넌트는 앱 Navigation에 의존하지 않습니다. 컴포넌트 Preview는 `:core:preview`의 재사용 enum parameter provider를 사용합니다.

## 타이포그래피

YAPP 앱의 Compose 타이포그래피 기준은 [YAPP Figma Typography 페이지](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=3-50739)입니다. 구현은 `:core:designsystem`의 `YappTypography` 토큰과 `YappTheme`의 Material 3 타이포그래피 매핑을 사용합니다.

모든 스타일은 Pretendard JP와 Figma에서 지정한 `"ss10" 1` 글꼴 기능을 사용합니다. 크기·행간·자간은 Compose `sp` 단위이며, 자간은 Figma 글자 크기에 백분율을 적용한 절대값입니다.

| Figma 토큰 | 글꼴 굵기 | 크기 | 행간 | 자간 |
| --- | ---: | ---: | ---: | ---: |
| Display 1 / Bold | 700 | 56 | 72 | -1.7864 |
| Display 2 / Bold | 700 | 40 | 52 | -1.128 |
| Title 1 / Bold | 700 | 36 | 48 | -0.972 |
| Title 2 / Bold | 700 | 28 | 38 | -0.6608 |
| Title 3 / Bold | 700 | 24 | 32 | -0.552 |
| Heading 1 / Bold | 600 | 22 | 30 | -0.4268 |
| Heading 2 / Bold | 600 | 20 | 28 | -0.24 |
| Headline 1 / Regular | 400 | 18 | 26 | -0.0036 |
| Headline 1 / Bold | 600 | 18 | 26 | -0.0036 |
| Headline 2 / Bold | 600 | 17 | 24 | 0 |
| Body 1 / Normal Regular | 400 | 16 | 24 | 0.0912 |
| Body 1 / Normal Medium | 500 | 16 | 24 | 0.0912 |
| Body 1 / Normal Bold | 600 | 16 | 24 | 0.0912 |
| Body 1 / Reading Regular | 400 | 16 | 26 | 0.0912 |
| Body 2 / Normal Regular | 400 | 15 | 22 | 0.144 |
| Body 2 / Normal Medium | 500 | 15 | 22 | 0.144 |
| Body 2 / Normal Bold | 600 | 15 | 22 | 0.144 |
| Body 2 / Reading Regular | 400 | 15 | 24 | 0.144 |
| Label 1 / Normal Medium | 500 | 14 | 20 | 0.203 |
| Label 1 / Normal Bold | 600 | 14 | 20 | 0.203 |
| Label 2 / Regular | 400 | 13 | 18 | 0.2522 |
| Label 2 / Medium | 500 | 13 | 18 | 0.2522 |
| Label 2 / Bold | 600 | 13 | 18 | 0.2522 |
| Caption 1 / Bold | 600 | 12 | 16 | 0.3024 |
| Caption 2 / Bold | 600 | 11 | 14 | 0.3421 |

Figma의 Bold 표기는 해당 스타일의 실제 글꼴 굵기를 따릅니다. 본문·레이블의 Bold와 헤딩·캡션의 Bold는 Pretendard JP SemiBold(600)입니다.

앱에서 Figma 스타일을 직접 쓸 때는 `YappTypography`의 대응 속성을 사용합니다. `MaterialTheme.typography` 소비자를 위해 Material 3 15개 슬롯은 이 토큰들에 연결됩니다. 새 색상·컴포넌트 토큰은 해당 Figma 기준이 확인될 때 별도 결정으로 추가합니다.

글꼴은 공식 [Pretendard 저장소 v1.3.9](https://github.com/orioncactus/pretendard/tree/v1.3.9)에서 가져오며, 재배포를 위해 `:core:designsystem/src/main/assets/fonts/OFL-1.1.txt`를 함께 둡니다.
