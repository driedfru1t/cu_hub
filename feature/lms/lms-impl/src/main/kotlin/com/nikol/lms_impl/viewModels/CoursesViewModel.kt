package com.nikol.lms_impl.viewModels

import arrow.core.Either
import com.nikol.domain.NoParam
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.useCase.GetCoursesUseCase
import com.nikol.lms_impl.mvi.effect.CoursesEffect
import com.nikol.lms_impl.mvi.intent.CoursesIntent
import com.nikol.lms_impl.mvi.state.CoursesState
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.listen
import direct.direct_core.on
import direct.direct_core.onLatest
import direct.direct_core.onSingle
import javax.inject.Inject

interface CoursesRouter : Router {
    fun toCourse(id: Int, name: String)

    fun toMoreInfo(id: Int, name: String)
}

class CoursesViewModel @Inject constructor(
    val getCoursesUseCase: GetCoursesUseCase
) : RouterViewModel<CoursesIntent, CoursesState<CourseSummary>, CoursesEffect, CoursesRouter>() {
    override fun createInitialState(): CoursesState<CourseSummary> = CoursesState.Loading()

    private var emissionCount = 0

    override fun handleIntents() = intents {
        listen(getCoursesUseCase(NoParam)) { either ->
            when (either) {
                is Either.Left -> {
                    setEffect { CoursesEffect.ShowError(either.value) }
                    setState { CoursesState.Success(courses = courses) }
                }

                is Either.Right -> {
                    val dbData = either.value
                    emissionCount++
                    setState {
                        if (emissionCount == 1) {
                            CoursesState.Loading(courses = dbData)
                        } else {
                            CoursesState.Success(courses = dbData)
                        }
                    }
                }
            }
        }
//        listen(
//            source = state
//                .mapLatest { it.currentPage }
//                .distinctUntilChanged()
//                .flatMapLatest { getCoursesUseCase(NoParam) }
//        ) { either ->
//            Log.d("TT", "привет")
//            when (either) {
//                is Either.Left -> {
//                    setEffect { CoursesEffect.ShowError(either.value) }
//                    setState { CoursesState.Success(courses = courses) }
//                }
//
//                is Either.Right -> {
//                    val dbData = either.value
//                    emissionCount++
//                    setState {
//                        if (emissionCount == 1) {
//                            CoursesState.Loading(courses = dbData)
//                        } else {
//                            CoursesState.Success(courses = dbData)
//                        }
//                    }
//                }
//            }
//        }
        onSingle<CoursesIntent.Load> {}
        onLatest<CoursesIntent.Refresh> {}

        on<CoursesIntent.ChangeTab> { tab ->
            setState {
                when (this) {
                    is CoursesState.Loading -> copy(currentPage = tab.participationType)
                    is CoursesState.Success -> copy(currentPage = tab.participationType)
                }
            }
        }

        onNavigate<CoursesIntent.ClickToCourse> { toCourse(it.id, it.name) }

        onNavigate<CoursesIntent.ClickToMore> { toMoreInfo(it.id, it.name) }
    }
}