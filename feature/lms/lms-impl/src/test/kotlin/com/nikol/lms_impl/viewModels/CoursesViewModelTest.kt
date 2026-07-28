package com.nikol.lms_impl.viewModels

import arrow.core.Either
import com.nikol.domain.NoParam
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseSettings
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.PublicationState
import com.nikol.lms.domain.useCase.GetCoursesUseCase
import com.nikol.lms_impl.mvi.intent.CourseDetailIntent
import com.nikol.lms_impl.mvi.intent.CoursesIntent
import com.nikol.lms_impl.mvi.state.CoursesState
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import io.mockk.Runs
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}

class RouterRule<R : Router>(
    private val routerClass: KClass<R>,

    ) : TestWatcher() {

    lateinit var mock: R
        private set

    private var attachedViewModel: RouterViewModel<*, *, *, R>? = null

    override fun starting(description: Description) {
        mock = mockkClass(routerClass, relaxed = true)
    }

    fun <VM : RouterViewModel<*, *, *, R>> attach(viewModel: VM): VM {
        attachedViewModel = viewModel
        viewModel.attachRouter(mock)
        return viewModel
    }

    override fun finished(description: Description) {
        attachedViewModel?.detachRouter()
        attachedViewModel = null
    }
}


class CoursesViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @get:Rule
    val routerRule = RouterRule(CoursesRouter::class)
    lateinit var viewModel: CoursesViewModel
    lateinit var useCase: GetCoursesUseCase

    @Before
    fun setup() {
        useCase = mockk(relaxed = true)
        viewModel = routerRule.attach(CoursesViewModel(useCase))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `при создании сразу находимся в состоянии загрузки`() = runTest {
        assertEquals(CoursesState.Loading(), viewModel.state.value)
    }

    @Test
    fun `прсле прихода пустых данных должена быть загрузка`() = runTest {

        every { useCase.invoke(NoParam) } returns flow {
            emit(Either.Right(emptyList()))
        }

        assertIs<CoursesState.Loading<CourseOverview>>(
            viewModel.state.value,
            "Состояние после приходя пустых данныых из бд должно быть Loading"
        )
    }

    @Test
    fun `навигация на детали`() {
        viewModel.setIntent(CoursesIntent.ClickToCourse(1, "English"))
        verify {
            routerRule.mock.toCourse(1, "English")
        }
    }
}