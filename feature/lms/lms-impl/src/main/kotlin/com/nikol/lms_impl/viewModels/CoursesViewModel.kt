package com.nikol.lms_impl.viewModels

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.domain.NoParam
import com.nikol.lms.domain.useCase.GetCoursesUseCase
import com.nikol.lms_impl.mvi.effect.CoursesEffect
import com.nikol.lms_impl.mvi.intent.CoursesIntent
import com.nikol.lms_impl.mvi.state.CoursesState
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.listen
import direct.direct_core.onLatest
import direct.direct_core.onSingle
import javax.inject.Inject

interface CoursesRouter : Router {
    fun toCourse(id: Int)
    fun toAuth()
}

class CoursesViewModel @Inject constructor(
    val getCoursesUseCase: GetCoursesUseCase
) : RouterViewModel<CoursesIntent, CoursesState, CoursesEffect, CoursesRouter>() {
    override fun createInitialState(): CoursesState = CoursesState.Loading


    override fun handleIntents() = intents {
        listen(getCoursesUseCase(NoParam)) { either ->
            when (either) {
                is Either.Left -> {
                    setEffect { CoursesEffect.ShowError(either.value) }
                }

                is Either.Right -> {
                    val dbData = either.value

                    setState {
                        if (dbData.isEmpty()) {
                            CoursesState.Loading
                        } else {
                            if (this@setState is CoursesState.Refreshing) {
                                CoursesState.Refreshing(courses = dbData)
                            } else {
                                CoursesState.Success(courses = dbData)
                            }
                        }
                    }
                }
            }
        }
        onSingle<CoursesIntent.Load> { }
        onLatest<CoursesIntent.Refresh> { }
    }
}