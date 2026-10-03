# 기여 및 브랜치 운영 규칙

이 저장소는 Git Flow를 사용합니다. 모든 변경은 이슈 또는 작업 기록과 연결하고 PR로 병합합니다.

## 장기 브랜치

| 브랜치 | 용도 | 병합 대상 |
| --- | --- | --- |
| `main` | 배포 가능한 버전과 릴리즈 태그 | `release/*`, `hotfix/*` |
| `develop` | 다음 버전의 통합 작업 | `feature/*`, `fix/*`, `chore/*`, `release/*`, `hotfix/*` |

`main`과 `develop`에는 직접 푸시하지 않습니다. PR의 리뷰와 대화 해결을 거쳐 병합합니다.

## 작업 브랜치

| 패턴 | 생성 기준 | PR 대상 |
| --- | --- | --- |
| `feature/<이슈번호>-<설명>` | `develop` | `develop` |
| `fix/<이슈번호>-<설명>` | `develop` | `develop` |
| `chore/<이슈번호>-<설명>` | `develop` | `develop` |
| `release/v<버전>` | `develop` | `main`, 이후 `develop` |
| `hotfix/v<버전>` | `main` | `main`, 이후 `develop` |

설명은 소문자 영문과 하이픈을 사용합니다. 예: `feature/42-attendance-check-in`.

## PR 작성 규칙

PR 제목은 변경 내용을 한국어로 작성합니다. `chore:`, `feat:`, `fix:`처럼 작업 유형을 나타내는 접두어는 붙이지 않습니다. 기술명과 고유명사는 원래 표기를 사용할 수 있습니다. 예: `Codex 슬래시 커맨드 추가`.

이 규칙은 PR 제목에 적용합니다. 커밋 메시지의 유형 접두어와 작업 브랜치 이름은 기존 규칙을 따릅니다.

## 개발과 배포

1. `develop`에서 작업 브랜치를 만들고 변경을 커밋합니다.
2. `develop`을 대상으로 PR을 열고 관련 이슈, 검증 결과, 화면 변경 사항을 기록합니다.
3. 일반 작업 PR은 Squash and merge로 병합합니다.
4. 배포 준비가 되면 `develop`에서 `release/v<버전>`을 만듭니다. 릴리즈 브랜치에서는 버전 조정과 배포 준비 수정만 수행합니다.
5. 릴리즈 브랜치를 `main`에 Merge commit으로 병합하고, `main`의 해당 커밋에 `v<버전>` 태그를 만듭니다.
6. 릴리즈 과정의 수정 사항을 반영하도록 릴리즈 브랜치를 `develop`에도 Merge commit으로 병합합니다.

긴급 수정은 `main`에서 `hotfix/v<버전>`을 만들고 검증 후 `main`에 Merge commit으로 병합합니다. `main`에 버전 태그를 만든 뒤 같은 핫픽스 브랜치를 `develop`에도 Merge commit으로 병합합니다. 릴리즈나 핫픽스가 양쪽 브랜치에 반영되기 전에는 해당 브랜치를 삭제하지 않습니다.

버전 번호는 배포 시점에 결정하며, 릴리즈가 아닌 작업에는 Git 태그를 만들지 않습니다.

## 로컬 검증

Android 변경 사항은 PR을 열기 전에 최소한 아래 명령으로 빌드합니다. 해당 변경과 관련된 테스트가 있다면 함께 실행합니다.

```bash
./gradlew :app:assembleDebug :app:lintDebug
```
