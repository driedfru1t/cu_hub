package com.nikol.lms_impl.viewModels

import arrow.optics.copy
import arrow.optics.optics
import arrow.optics.set
import com.nikol.lms.data.local.TaskStateStorage
import com.nikol.lms.domain.error.TaskError
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms.domain.model.TaskSummary
import com.nikol.lms.domain.repo.TaskRepository
import com.nikol.lms.domain.useCase.GetTasksUseCase
import com.nikol.lms.domain.useCase.TaskParam
import com.nikol.ui.getActiveAppLocale
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.DirectIntent
import direct.direct_core.DirectState
import direct.direct_core.on
import direct.direct_core.onSingle
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.immutableSetOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import javax.inject.Inject

interface TasksRouter : Router {
    fun onBack()
    fun onTask(id: Int)
}

@optics
data class WeekTasksUi(
    val offset: Int,
    val weekRangeStr: String,
    val days: ImmutableList<DayTasksUi>
) {
    companion object
}

@optics
data class DayTasksUi(
    val dateEpoch: Long,
    val dayHeader: String,
    val tasks: ImmutableList<TaskSummary>
) {
    companion object
}

@optics
data class TasksState(
    val weeksMap: Lce<TaskError, ImmutableMap<Int, WeekTasksUi>> = Lce.Loading,
    val windowOffset: Int = 0,
    val isRefresh: Boolean = false,
    val taskSate: ImmutableSet<TaskState> = persistentSetOf(),
    val showBottomSheet: Boolean = false
) : DirectState {
    companion object
}

sealed interface TasksIntent : DirectIntent {
    data object Refresh : TasksIntent
    data class OnTaskClick(val id: Int) : TasksIntent
    data object OnBack : TasksIntent
    data class ShiftWindow(val direction: Int) : TasksIntent
    data object OpenFilter : TasksIntent
    data object CloseFilter : TasksIntent
    data class SetTaskStates(
        val states: ImmutableSet<TaskState>
    ) : TasksIntent

    data object Init : TasksIntent
}

typealias TasksStore = RouterViewModel<TasksIntent, TasksState, DirectEffect, TasksRouter>

class TasksVM @Inject constructor(
    private val taskRepository: TaskRepository,
    private val getTasksUseCase: GetTasksUseCase,
    private val taskStateStorage: TaskStateStorage
) : TasksStore() {

    companion object {
        const val CENTER_INDEX = 10
        const val TOTAL_WEEKS = 21
        const val SHIFT_STEP = 5
        const val INITIAL_PAGE = 50_000
        const val TOTAL_PAGES = 100_000
    }

    override fun createInitialState(): TasksState = TasksState()

    override fun handleIntents() = intents {
        listen(
            state
                .map { it.taskSate to it.windowOffset }
                .distinctUntilChanged()
                .flatMapLatest { pair ->
                    val zoneId = ZoneId.systemDefault()
                    val currentMonday = LocalDate.now(zoneId).with(DayOfWeek.MONDAY)
                    val centerMonday = currentMonday.plusWeeks(pair.second.toLong())

                    val startFrom = centerMonday
                        .minusWeeks(CENTER_INDEX.toLong())
                        .atStartOfDay(zoneId)
                        .toInstant()

                    val endTo = centerMonday
                        .plusWeeks((TOTAL_WEEKS - CENTER_INDEX).toLong())
                        .atStartOfDay(zoneId)
                        .toInstant()

                    taskRepository.observeTasks(
                        states = state.value.taskSate.toList(),
                        courseIds = emptyList(),
                        from = startFrom,
                        to = endTo
                    )
                }
        ) {
            latest { tasks ->
                val currentOffset = state.value.windowOffset

                val uiWeeksMap = buildWeekUiMap(
                    tasks = tasks,
                    windowOffset = currentOffset
                )

                setState {
                    copy {
                        TasksState.weeksMap set Lce.Content(uiWeeksMap.toImmutableMap())
                    }
                }
            }
        }

        onSingle<TasksIntent.Refresh> {
            setState { copy { TasksState.isRefresh set true } }
            getTasksUseCase(TaskParam(emptyList(), emptyList()))
            setState { copy { TasksState.isRefresh set false } }
        }

        onNavigate<TasksIntent.OnTaskClick> { onTask(it.id) }
        onNavigate<TasksIntent.OnBack> { onBack() }

        onSingle<TasksIntent.ShiftWindow> { intent ->
            val direction = intent.direction
            val newOffset = state.value.windowOffset + (direction * SHIFT_STEP)
            setState {
                copy {
                    TasksState.windowOffset set newOffset
                }
            }
        }
        on<TasksIntent.OpenFilter> {
            setState { copy { TasksState.showBottomSheet set true } }
        }
        on<TasksIntent.CloseFilter> {
            setState { copy { TasksState.showBottomSheet set false } }
        }
        onSingle<TasksIntent.SetTaskStates> { intent ->
            setState { copy { TasksState.taskSate set intent.states } }
            taskStateStorage.saveStates(intent.states.toList())
        }
        on<TasksIntent.Init> {
            val states = taskStateStorage.getStates().first()
            setState { copy { TasksState.taskSate set states.toImmutableSet() } }
        }
    }

    init {
        setIntent(TasksIntent.Init)
    }

    private fun buildWeekUiMap(
        tasks: List<TaskSummary>,
        windowOffset: Int
    ): Map<Int, WeekTasksUi> {
        val zoneId = ZoneId.systemDefault()
        val locale = getActiveAppLocale()
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", locale)
        val currentMonday = LocalDate.now(zoneId).with(DayOfWeek.MONDAY)

        return (0 until TOTAL_WEEKS).associate { pageIndex ->
            val offset = pageIndex - CENTER_INDEX + windowOffset

            val monday = currentMonday.plusWeeks(offset.toLong())
            val sunday = monday.plusDays(6)

            val weekRangeStr = "${monday.format(dateFormatter)} — ${sunday.format(dateFormatter)}"

            val weekTasks = tasks.filter { task ->
                val taskDate = task.deadline.atZone(zoneId).toLocalDate()
                !taskDate.isBefore(monday) && !taskDate.isAfter(sunday)
            }

            val days = weekTasks
                .groupBy { it.deadline.atZone(zoneId).toLocalDate() }
                .toSortedMap()
                .map { (date, dailyTasks) ->
                    DayTasksUi(
                        dateEpoch = date.toEpochDay(),
                        dayHeader = date.dayOfWeek
                            .getDisplayName(TextStyle.FULL, locale)
                            .uppercase(),
                        tasks = dailyTasks.sortedBy { it.deadline }.toImmutableList()
                    )
                }.toImmutableList()

            offset to WeekTasksUi(
                offset = offset,
                weekRangeStr = weekRangeStr,
                days = days
            )
        }
    }

    fun getWeekTitle(offset: Int): String {
        val zoneId = ZoneId.systemDefault()
        val locale = getActiveAppLocale()
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", locale)
        val currentMonday = LocalDate.now(zoneId).with(DayOfWeek.MONDAY)
        val monday = currentMonday.plusWeeks(offset.toLong())
        val sunday = monday.plusDays(6)
        return "${monday.format(dateFormatter)} — ${sunday.format(dateFormatter)}"
    }
}