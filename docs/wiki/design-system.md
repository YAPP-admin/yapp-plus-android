---
authority: canonical
status: active
last_verified: 2026-10-08
---

# 디자인시스템

## 색상

공용 Compose 색상 토큰은 `:core:designsystem`의 `theme/Color.kt`에 있는 `YappColor`에서 제공합니다. Figma Components 기준으로 이미 구현한 ARGB 값과 알파값을 보존하면서 컴포넌트와 Preview가 같은 토큰을 사용합니다. 컴포넌트 파일에 고정 색상 리터럴이나 로컬 색상 상수를 새로 두지 않습니다.

[로그인·회원가입 Figma](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487)의 변형은 별도 색상 토큰을 사용합니다. 기존 `primary`와 공용 컴포넌트 기본값을 바꾸지 않습니다.

Feature의 제목·설명·링크·배경에는 공개 `YappAuthColor`의 `brand`, `textPrimary`, `textSecondary`, `textSubtle`, `textFaint`, `background`를 사용합니다. 나머지 공용 색상과 컴포넌트 내부 색상은 `YappColor`에서 관리합니다.

메인 탭 데모의 배경·표면·본문·구분선 색상은 `:core:designsystem/theme/YappMainColor.kt`에서 관리합니다. 인증 화면에서 사용하는 기존 색상 토큰은 변경하지 않습니다.

| 토큰 | 값 | 사용처 |
| --- | --- | --- |
| `brand` | `#FF6600` | CTA·Compact 팝업의 주요 버튼 |
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

## 하단 내비게이션

`:core:designsystem/component/navigation/`의 `YappBottomNavigation`은 탭 배치·상단 구분선을, `YappBottomNavigationItem`은 아이콘·라벨·선택 상태와 탭 semantics를 담당합니다. 호출자가 아이콘 리소스, 표시 라벨, 선택 상태와 클릭 콜백을 전달하며, Feature 상태나 App Navigation에는 의존하지 않습니다. 컴포넌트 Preview는 `:core:preview`의 재사용 enum parameter provider를 사용합니다.

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
