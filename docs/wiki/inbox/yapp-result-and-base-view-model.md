---
type: knowledge-candidate
authority: none
status: candidate
created_at: 2026-11-24
updated_at: 2026-11-24
use_count: 0
last_used_at: null
target_path: null
resolution_reason: null
---

# YappResult와 BaseViewModel 설계안

일정 탭과 홈은 같은 Repository에서 일정을 읽고 각자 `NoActiveGenerationException`과 일반 오류를 처리한다. Data가 일정 조회 결과의 의미를 한 번 정하고, 각 화면은 그 결과를 자신의 `UiState`로 변환하도록 제안한다. [현재 일정 ViewModel](../../../feature/main/schedule/src/main/java/com/yapp/plus/feature/main/schedule/ScheduleViewModel.kt) · [현재 홈 ViewModel](../../../feature/main/home/src/main/java/com/yapp/plus/feature/main/home/HomeViewModel.kt)

| 위치 제안 | 책임 |
| --- | --- |
| `:domain`의 `com.yapp.plus.domain.result` | Repository가 한 번의 호출을 마치면 반환하는 `YappResult` |
| `:data` | DTO를 Domain 값으로 변환하고 통신 오류와 서버 응답을 기능별 실패 사유로 해석 |
| 새 Android 라이브러리 `:core:ui` | `:domain`, Lifecycle, Coroutines에 의존하는 `BaseViewModel` |
| `:feature:main:schedule`, `:feature:main:home` | 요청별 로딩, 결과 표시, 재시도·중복 입력 정책 |

두 Feature가 `:core:ui` 모듈을 공유한다. 이 모듈은 공개 시그니처에 쓰는 Domain, Lifecycle ViewModel, Coroutines 타입을 Gradle의 `api` 의존성으로 제공한다.

## YappResult와 일정 조회 계약

`T`는 성공 값, `E`는 호출자가 처리할 실패 사유다. 변경 API는 성공 값으로 `Unit`을 쓸 수 있고, 값이 없을 수 있는 조회는 `Success(null)`을 표현할 수 있다. 로딩은 요청을 시작하는 화면에서 관리한다.

```kotlin
sealed interface YappResult<out T, out E : Any> {
    data class Success<out T>(
        val value: T,
    ) : YappResult<T, Nothing>

    data class Failure<out E : Any>(
        val reason: E,
    ) : YappResult<Nothing, E>
}
```

일정에서 구분할 실패 사유는 두 가지다. 기능마다 자신의 오류 타입을 쓰므로 가입 입력 오류처럼 추가 값이 필요한 경우에도 해당 계약에 필드 정보를 담을 수 있다.

```kotlin
enum class ScheduleFailure {
    NoActiveGeneration,
    Unavailable,
}
```

```kotlin
interface SessionScheduleRepository {
    suspend fun fetchCurrentWeekSchedule(): YappResult<CurrentWeekSchedule, ScheduleFailure>

    suspend fun fetchCurrentGenerationSchedule():
        YappResult<CurrentGenerationSchedule, ScheduleFailure>
}
```

### Data에서 결과 만들기

현재 일정 DataSource는 HTTP 404와 서버 코드 `CURRENT_GENERATION_NOT_FOUND`가 함께 온 경우에만 `NoActiveGenerationException`을 던진다. 이 신호는 `invokeRequest`가 일반 예외로 분류하기 전에 처리한다. 그 밖의 통신·응답 오류는 기존 `NetworkRequestResult.Error`에서 `Unavailable`로 바꾼다. `invokeRequest`가 다시 던지는 `CancellationException`은 호출 코루틴까지 전파된다. 각 Feature는 Domain 타입만 참조한다. [일정 DataSource](../../../data/src/main/java/com/yapp/plus/data/session/SessionScheduleRemoteDataSource.kt) · [네트워크 요청 처리](../../../core/network/src/main/java/com/yapp/plus/core/network/NetworkRequestResult.kt)

```kotlin
class SessionScheduleRepositoryImpl(
    private val dataSource: SessionScheduleDataSource,
) : SessionScheduleRepository {
    override suspend fun fetchCurrentWeekSchedule() =
        fetchScheduleResult {
            CurrentWeekScheduleMapper.toDomain(dataSource.fetchCurrentWeekSchedule())
        }

    override suspend fun fetchCurrentGenerationSchedule() =
        fetchScheduleResult {
            CurrentGenerationScheduleMapper.toDomain(dataSource.fetchCurrentGenerationSchedule())
        }

    private suspend fun <T> fetchScheduleResult(
        fetch: suspend () -> T,
    ): YappResult<T, ScheduleFailure> {
        val result = invokeRequest<YappResult<T, ScheduleFailure>> {
            try {
                YappResult.Success(fetch())
            } catch (_: NoActiveGenerationException) {
                YappResult.Failure(ScheduleFailure.NoActiveGeneration)
            }
        }
        return when (result) {
            is NetworkRequestResult.Success -> result.data
            is NetworkRequestResult.Error -> YappResult.Failure(ScheduleFailure.Unavailable)
        }
    }
}
```

## BaseViewModel과 화면 상태

`BaseViewModel<S>`은 생성자로 초기 상태를 받고 읽기 전용 `StateFlow<S>`를 공개한다. `launchRequest`는 요청을 시작하고 성공·실패에 맞는 상태 갱신 함수를 실행한 뒤 종료한다. 반환한 `Job`은 화면이 요청별로 보관한다. Hilt 주입은 각 화면의 ViewModel에서 한다.

```kotlin
abstract class BaseViewModel<S : Any>(initialState: S) : ViewModel() {
    private val mutableUiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = mutableUiState.asStateFlow()

    protected fun updateState(transform: (S) -> S) {
        mutableUiState.update(transform)
    }

    protected fun <T, E : Any> launchRequest(
        request: suspend () -> YappResult<T, E>,
        onStart: (S) -> S,
        onSuccess: (S, T) -> S,
        onFailure: (S, E) -> S,
    ): Job = viewModelScope.launch {
        updateState(onStart)
        val result = request()
        ensureActive()
        when (result) {
            is YappResult.Success -> updateState { onSuccess(it, result.value) }
            is YappResult.Failure -> updateState { onFailure(it, result.reason) }
        }
    }
}
```

상태 갱신 함수는 **응답 시점의 최신 상태**를 받아 새 상태를 계산한다. 동시에 값이 바뀌면 `update`가 함수를 다시 실행할 수 있으므로 네트워크 호출, 저장, 화면 이동은 이 함수 밖에서 수행한다. `ensureActive()`는 취소된 요청이 늦게 반환했을 때 결과 반영을 중단한다. 화면이 살아 있는 동안 요청만 취소하면서 로딩을 해제해야 한다면, 해당 화면이 그 요청의 상태를 복원한다. [StateFlow.update](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/update.html) · [ensureActive](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/ensure-active.html)

## 일정 탭에서 사용하기

기존 초기 조회와 `retry()` 진입점을 사용한다. 두 화면의 조회 이벤트는 메인 스레드에서 처리하고, 같은 조회가 진행 중일 때 들어온 재시도는 한 요청으로 합친다. 세션 목록이 비어 있으면 기존 `SessionScheduleUiMapper`가 정상 상태인 `Empty`를 만든다. [일정 UI 변환](../../../feature/main/schedule/src/main/java/com/yapp/plus/feature/main/schedule/model/SessionScheduleUiMapper.kt)

```kotlin
@HiltViewModel
internal class ScheduleViewModel
    @Inject
    constructor(
        private val repository: SessionScheduleRepository,
    ) : BaseViewModel<ScheduleUiState<List<ScheduleSessionUiModel>>>(ScheduleUiState.Loading) {
        private var scheduleJob: Job? = null

        init {
            retry()
        }

        fun retry() {
            if (scheduleJob?.isActive == true) return
            scheduleJob = launchRequest(
                request = repository::fetchCurrentGenerationSchedule,
                onStart = { ScheduleUiState.Loading },
                onSuccess = { _, schedule -> SessionScheduleUiMapper.toUiState(schedule) },
                onFailure = { _, reason ->
                    when (reason) {
                        ScheduleFailure.NoActiveGeneration -> ScheduleUiState.Inactive
                        ScheduleFailure.Unavailable -> ScheduleUiState.Error
                    }
                },
            )
        }
    }
```

## 홈에서 사용하기

`HomeViewModel`은 `BaseViewModel<HomeUiState>(HomeUiState())`를 상속한다. 이번 주 일정과 주간 공지는 각각의 `Job`으로 실행한다. 아래 일정 요청은 최신 `HomeUiState`의 `currentWeekSchedule`만 갱신하므로, 먼저 도착한 공지 응답도 화면에 남는다. [홈 상태](../../../feature/main/home/src/main/java/com/yapp/plus/feature/main/home/model/HomeUiState.kt)

```kotlin
fun retryCurrentWeekSchedule() {
    if (scheduleJob?.isActive == true) return
    scheduleJob = launchRequest(
        request = sessionScheduleRepository::fetchCurrentWeekSchedule,
        onStart = { it.copy(currentWeekSchedule = AttendanceUiState.Loading) },
        onSuccess = { state, schedule ->
            state.copy(currentWeekSchedule = CurrentWeekSessionUiMapper.toUiState(schedule))
        },
        onFailure = { state, reason ->
            val schedule = when (reason) {
                ScheduleFailure.NoActiveGeneration -> AttendanceUiState.Inactive
                ScheduleFailure.Unavailable -> AttendanceUiState.Error
            }
            state.copy(currentWeekSchedule = schedule)
        },
    )
}
```

## 설계 검증 기준

| 조건 | 관찰할 결과 |
| --- | --- |
| 기수는 있고 일정 목록이 비어 있음 | Repository는 `Success`, 일정 탭은 `Empty` |
| 404와 `CURRENT_GENERATION_NOT_FOUND`가 함께 옴 | 두 일정 조회 모두 `Failure(NoActiveGeneration)`, 두 화면 모두 `Inactive` |
| 다른 404 또는 네트워크 실패 뒤 다시 조회해 성공함 | `Error → Loading → Content` 또는 `Empty` |
| 같은 일정 조회가 진행되는 동안 재시도함 | Repository 호출 1회 |
| 홈 일정과 공지가 서로 다른 순서로 완료됨 | 두 요청의 최종 상태를 모두 표시하고, 둘 다 완료되면 초기 로딩 종료 |
| 취소되거나 취소 직후 결과가 늦게 도착함 | 성공·실패 갱신 함수 호출 0회, 새 요청의 상태 보존 |

문서의 Kotlin 예시 7개를 Kotlin 2.2.10으로 별도 JVM 환경에서 컴파일했다. Ktor MockEngine으로 두 일정 API의 정상·빈 응답이 화면까지 전달되는 흐름과 HTTP 오류 분류를 검증했다. Fake로는 취소 예외 전파, 일정 재시도·중복 요청, 홈의 두 완료 순서와 취소 뒤 지연 응답을 확인했다.

## 실제 사용 이력

| 작업 ID 또는 이슈 | 사용일 | 맥락 | 실제 판단 근거 |
| --- | --- | --- | --- |
