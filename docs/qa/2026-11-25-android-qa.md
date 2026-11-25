# 2026-11-25 YAPP Android QA

대상은 `feature/238-afterparty-community` 작업트리입니다. 기준 커밋 `47818f7`에 로컬 뒤풀이·정산 연결, 리플 제거, 커뮤니티 로딩 수정, 출결 보고 선택 표시 수정이 포함된 상태에서 확인했습니다. Android 로그인·가입, 메인 5개 탭, 상세 화면, 설정을 점검했습니다. QA 결과는 작업트리의 상태를 설명합니다.

## 비교 기준

- [Figma 앱 화면](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=9-58487)에서 로그인·가입, 홈·출결 보고·팀 사진·알림, 일정, 게시판 목록·상세, 커뮤니티, My·설정의 관련 프레임을 확인했습니다.
- [Notion 화면 및 기능별 정책 2026](https://app.notion.com/p/yapp-workspace/2026-3fa2106a0e84800f9abfef181b090892) 원문은 Notion 데스크톱 앱에서 세션, 출석 대상·시간·점수, 가입과 미결정 항목을 읽었습니다. 연결에서 열리지 않은 [용어 정의서](https://app.notion.com/p/yapp-workspace/2026-3fa2106a0e84802e8e37d0e9ffee3fb9)와 원문에서 읽지 못한 세부 항목은 [로컬 정책](../wiki/product-policy.md)과 [결정 기록](../wiki/decisions.md)으로 보완했습니다. 원문을 확인한 범위만 Notion 검증으로 집계합니다.
- [백엔드 dev API 계약 `4c6bbf7`](https://github.com/YAPP-admin/yapp-plus-backend/blob/4c6bbf7351e5433e7e0a07858eca6ace52102ace/APIDOC.md)에서 사용자 API의 실제 응답 및 이동 계약을 확인했습니다. API 계약은 실행 중인 운영 서버의 배포 상태와 구분합니다.
- API 36 에뮬레이터에서 기준 화면 14개를 캡처해 주요 프레임과 비교했습니다. 샘플 데이터 화면은 레이아웃·색상·문구를 확인하는 데 사용했습니다. 실제 화면 경로와 상태 전이는 별도의 Android 테스트에서 확인했습니다.

## 수정이 필요한 항목

| 우선순위 | 재현 조건과 차이 | 근거 |
| --- | --- | --- |
| 높음 | **세션 자동 알림에서 일정 이동 누락.** `SESSION_REMINDER` 알림은 알림함에 내용이 표시되지만 눌러도 일정으로 이동하지 않습니다. 푸시도 세션 ID와 일정 이동 대상을 앱 시작 인자로 전달하지 않습니다. 알림함과 푸시 두 진입점에 영향이 있습니다. | [서버 일정 이동 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/4c6bbf7351e5433e7e0a07858eca6ace52102ace/APIDOC.md#세션-자동-알림과-일정-화면-이동), [알림 DTO](../../data/src/main/java/com/yapp/plus/data/notification/NotificationResponse.kt), [알림함 이동](../../feature/main/home/src/main/java/com/yapp/plus/feature/main/home/HomeNotificationsScreen.kt), [푸시 처리](../../app/src/main/java/com/yapp/plus/notification/PushNotifications.kt) |
| 보통 | **My에서 운영진 회원 구분 누락.** 본인 프로필이 `membershipStatus: STAFF`를 반환하면 Android는 `UNKNOWN`으로 변환하고 회원 구분을 `—`로 표시합니다. | [서버 프로필 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/4c6bbf7351e5433e7e0a07858eca6ace52102ace/APIDOC.md#L1448), [프로필 매핑](../../data/src/main/java/com/yapp/plus/data/profile/UserProfileData.kt), [My 표시](../../feature/main/my/src/main/java/com/yapp/plus/feature/main/my/components/MyProfile.kt) |
| 보통 | **가입 입력 화면 불일치.** 빈 이름 화면의 비활성 `다음`은 Figma에서 회색이며, Android 캡처에서는 연한 주황색입니다. 휴대폰 입력은 앱 명세를 반영한 Wiki의 `010-1234-5678` 형태와 달리 Android에서 `01012345678`로 표시합니다. 서버에 보내는 E.164 변환은 별도로 구현되어 있습니다. | [Figma 이름 입력](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=321-11558), [앱 명세 반영](../wiki/product-policy.md#앱-기능-명세서-ver-2), [가입 입력 코드](../../feature/signup/src/main/java/com/yapp/plus/feature/signup/SignUpContent.kt) |
| 보통 | **뒤풀이 투표 제출 버튼 위치.** Figma는 하단에 고정된 제출 버튼을 사용합니다. Android에서는 버튼이 참석 선택 바로 뒤에 놓여 상세 콘텐츠와 함께 스크롤됩니다. 이 차이는 단일 응답 정책에서도 발생합니다. | [Figma 뒤풀이 투표](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=1437-6177), [투표 화면](../../feature/main/community/src/main/java/com/yapp/plus/feature/main/community/AfterpartyRoute.kt) |
| 보통 | **뒤풀이 상세와 정산 현황의 중복 로딩 표시.** 투표한 사용자가 뒤풀이 상세를 열면 참석자와 정산 목록이 각각 로딩 표시를 렌더링할 수 있습니다. 정산자 화면에서도 수금 요약과 대상자 목록을 동시에 기다리면 같은 현상이 발생할 수 있습니다. 목록 첫 진입에서 두 개가 뜨던 문제는 수정됐으며 테스트로 확인했습니다. 이 두 상세 화면의 동시 로딩은 코드 경로로 확인했습니다. | [뒤풀이 상세](../../feature/main/community/src/main/java/com/yapp/plus/feature/main/community/AfterpartyRoute.kt), [정산 현황](../../feature/main/community/src/main/java/com/yapp/plus/feature/main/community/SettlementRoute.kt) |
| 보통 | **정산 알림의 접근성 안내가 이동 대상과 다름.** 알림함의 정산 알림은 정산 상세로 이동하지만 클릭 접근성 문구는 `공지 상세 보기`입니다. | [정산 알림 이동과 클릭 라벨](../../feature/main/home/src/main/java/com/yapp/plus/feature/main/home/HomeNotificationsScreen.kt), [알림 문구](../../feature/main/home/src/main/res/values/strings.xml) |

## 디자인과 기능 범위를 확인할 항목

| 영역 | 확인한 차이 | 현재 근거 |
| --- | --- | --- |
| 홈 거리 확인 | 세션 좌표가 있으면 홈 카드에 거리 확인 버튼이 생깁니다. 측정 후에는 결과 행과 재확인 버튼이 추가됩니다. 확인한 Figma 홈 프레임에서는 이 UI가 보이지 않습니다. | 기기에서 거리 조회를 제공하는 [DEC-0054](../wiki/decisions.md#dec-0054--세션-주소-표시와-기기-거리-확인)는 확인했습니다. 버튼 위치·문구·재확인 상태의 디자인 근거를 별도로 확인해야 합니다. [Figma 홈](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=418-16033), [QA 당시 거리 UI](https://github.com/YAPP-admin/yapp-plus-android/blob/8f63dcd54aaede921603e5c94decf60eeeda779e/feature/main/home/src/main/java/com/yapp/plus/feature/main/home/components/HomeSessionDistance.kt) |
| 커뮤니티 모임 | Figma에는 뒤풀이·모임 탭과 모임 목록·멤버·인증 화면이 있습니다. 현재 Android 커뮤니티는 뒤풀이 목록으로 바로 들어갑니다. | 관련 디자인은 확인했습니다. 검토한 사용자 API 문서에서 모임 계약을 확인하지 못했습니다. [뒤풀이 목록](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=1437-6052), [모임 디자인](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=1792-40377) |
| 투표 차수 | Figma에는 1차·2차 선택이 있습니다. Android는 회차 구분 없이 참석 또는 불참을 제출합니다. | 현 API는 뒤풀이당 `ATTENDING` 또는 `NOT_ATTENDING` 하나만 받습니다. 현재 단일 응답은 [DEC-0062](../wiki/decisions.md#dec-0062--사용자-뒤풀이정산-연결)의 구현 범위입니다. [Figma 투표](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=1437-6177), [응답 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/4c6bbf7351e5433e7e0a07858eca6ace52102ace/APIDOC.md#L1527-L1535) |
| 수금 알림 | 정산 현황 Figma에 수동 `송금 알림 보내기`가 있습니다. Android에는 자동 알림 안내가 있고 수동 버튼이 없습니다. | 서버 문서에서 생성 알림과 24시간 미납 리마인드를 확인했습니다. 검토한 사용자 API에 수동 발송 계약이 없습니다. [Figma 정산 현황](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=1755-17782), [정산 알림 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/4c6bbf7351e5433e7e0a07858eca6ace52102ace/APIDOC.md#L1586) |
| 알림함 읽음 강조 | Figma 알림 목록은 일부 행의 배경을 강조합니다. Android 목록에는 이 강조가 없습니다. | 현재 사용자 알림 응답에 읽음 필드가 없고 별도 읽음 API도 없습니다. 강조 기준을 확인해야 합니다. [Figma 알림](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=1282-5362), [알림 계약](https://github.com/YAPP-admin/yapp-plus-backend/blob/4c6bbf7351e5433e7e0a07858eca6ace52102ace/APIDOC.md#L13-L15) |

## 화면별 검증 범위

| 화면 | 확인한 동작과 비교 | 남은 확인 |
| --- | --- | --- |
| 시작·로그인·가입 | 로그인 시작·뒤로·복원, Google 진입 UI, 신규 가입 이름·전화번호·오류 화면과 입력 상태 보존을 확인했습니다. Figma의 Apple은 앱 명세상 iOS 대상입니다. Notion의 가입코드·기수 직접 입력과 오래된 승인 대기 화면은 이후 [가입 응답 계약과 Android 결정](../wiki/product-policy.md#회원가입과-승인)에서 달라졌습니다. | 실제 Google 계정 인증·신규 등록·운영진 승인 |
| 홈·코드 출석·사전 보고 | 주간 카드, 빠른 메뉴, 오류 안내, 4자리 입력, 서버 응답을 따르는 완료·검토 대기, 보고 유형과 사유 검증을 확인했습니다. 선택 테두리·라디오 링은 Figma의 검정 계열로 반영돼 있습니다. Notion의 미결정 감점 경계는 클라이언트가 판단하지 않습니다. | 운영 세션으로 출석·보고 제출 |
| 팀 사진 출석 | Figma의 사진 선택·촬영 안내와 앱의 관련 경로·미리보기·팀원 선택 코드를 확인했습니다. 대기·복귀 갱신과 일부 변환은 테스트로 확인했습니다. | 실기기 카메라, 업로드 URL, 서버 제출·운영진 승인·반려 |
| 일정 | [Figma 일정](https://www.figma.com/design/LUFGoDnVmwJT43QhecBI8x/YAPP-?node-id=222-40678)과 날짜·행·세션 유형을 비교했습니다. 일정 탭 이동과 모의 세션의 장소·주소, 누락 장소 표시를 확인했습니다. | 운영 기수의 전체 일정 |
| 게시판 | 목록·상세 화면을 비교했습니다. 카테고리 선택, 모의 상세·읽음 확인, 실패 시 기존 오류 화면을 테스트했습니다. | 운영 데이터의 첨부 링크 만료·외부 제출 링크 |
| 알림함·푸시 | 모의 공지 알림에서 상세 이동·복귀와 로그인 대기 이동을 확인했습니다. 정산·세션 알림은 사용자 계약과 코드 경로를 점검했습니다. | 실제 FCM 수신·정산 딥링크·세션 일정 이동 |
| 커뮤니티·정산 | 모의 목록의 단일 로딩, 당겨서 새로고침, 투표 후 참석자 조회, 개인 납부 표시를 실행했습니다. 목록·투표·정산 작성·수금 현황의 디자인은 프레임과 코드로 대조했습니다. | 증빙 업로드, 정산 생성·수정, 수금자 화면과 My 정산의 실제 데이터 |
| My·설정 | 샘플 My·설정 화면을 Figma와 비교했습니다. 점수·이력·규칙·빈 상태, 알림 설정 상태 보존, 로그아웃·탈퇴 확인·취소·실패·성공 후 이동을 모의 응답으로 확인했습니다. 면제권과 진행률은 [기존 미연동 정책](../wiki/product-policy.md#figma-기준-my와-설정-2026-10-29)을 따릅니다. 탈퇴 문구와 서버의 이력 비식별화·보존은 기존 승인된 차이입니다. | 실제 푸시 등록, 공개 약관 접근, 실제 서버 로그아웃·탈퇴 |
| 공통 UI | 탭 이동, 압축 화면의 설정 액션, 공통 버튼의 클릭·비활성화와 리플 제거를 확인했습니다. 정산 알림의 클릭 라벨은 위에 기록했습니다. | 실기기·태블릿·TalkBack 전체 탐색, 큰 글자 및 화면 회전 전수 확인 |

## 실행 결과

- Debug 앱·테스트 APK 빌드, Android Lint, 전체 ktlint, Wiki 검사와 diff 공백 검사 통과.
- 모듈 단위 테스트 202건 통과. API 36 기본 실행은 54건 통과와 위치 전제 조건에 따른 3건 건너뜀입니다. 위치 전용 실행 7건을 더해 대상 테스트 57개를 모두 실행했고, 위치 상태별 반복을 포함해 61건이 통과했습니다.
- 위치 전용 실행에서 정확한 위치의 거리 조회·코드 제출, 대략적 위치, 권한 거부, 위치 기능 OFF, 위치 미수신과 오래된 위치 거부를 확인했습니다. GPS 입력만 사용한 첫 거리 테스트는 fused 공급자가 값을 받지 못해 실패했으며, 에뮬레이터의 fused 테스트 공급자를 설정한 재실행에서 통과했습니다.
- 운영·개발 서버에 테스트 기록을 쓰는 가입 E2E는 제외했습니다. 그 외 사용자 API가 필요한 UI 테스트는 모의 응답을 사용했습니다. 실제 계정·운영 데이터·FCM·카메라의 전수 검증은 남아 있습니다.

재발 방지 절차는 [Figma·Notion 원본 확인](../wiki/README.md#figmanotion-원본-확인)과 [DEC-0065](../wiki/decisions.md#dec-0065--android-구현-전-figmanotion-원본-확인)에 기록했습니다.

## Android 후속 수정

| 순서 | 변경 사항 | 상태 |
| --- | --- | --- |
| 1 | 세션 자동 알림에서 해당 일정으로 이동 | [PR #242](https://github.com/YAPP-admin/yapp-plus-android/pull/242) |
| 2 | My에서 `STAFF`를 `운영진`으로 표시 | [PR #243](https://github.com/YAPP-admin/yapp-plus-android/pull/243) |
| 3 | 가입 화면의 비활성 버튼 색상과 휴대폰 번호 표시 수정 | [PR #244](https://github.com/YAPP-admin/yapp-plus-android/pull/244) |
| 4 | 뒤풀이 투표 제출 버튼 하단 고정 | [PR #255](https://github.com/YAPP-admin/yapp-plus-android/pull/255) 병합 |
| 5 | 뒤풀이 상세와 정산 현황의 중복 로딩 표시 제거 | [PR #256](https://github.com/YAPP-admin/yapp-plus-android/pull/256) 병합 |
| 6 | 정산 알림의 클릭 접근성 안내를 `정산 상세 보기`로 수정 | [PR #257](https://github.com/YAPP-admin/yapp-plus-android/pull/257) 병합 |

4–6번의 선행 기능인 Android 뒤풀이·정산 연결도 develop에 병합했습니다. [앱 연결 PR #254](https://github.com/YAPP-admin/yapp-plus-android/pull/254)

홈 거리 버튼·결과를 제거하고 출결 보고 안내를 세션 시작 전에 표시하도록 반영했습니다. [거리 UI 결정](../wiki/decisions.md#dec-0066--홈-세션-카드의-거리-조회-ui-제거) · [출결 보고 안내 결정](../wiki/decisions.md#dec-0067--홈-사전-보고-안내-표시-조건)
