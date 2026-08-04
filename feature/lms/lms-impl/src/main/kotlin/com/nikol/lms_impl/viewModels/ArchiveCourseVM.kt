package com.nikol.lms_impl.viewModels

import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import arrow.core.raise.context.bind
import arrow.core.raise.either
import com.nikol.domain.NoParam
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.useCase.GetArchiveCourses
import com.nikol.lms_impl.mvi.intent.CourseArchiveIntent
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.DirectEffect
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ArchiveRouter : Router {
    fun onBack()
    fun onDetail(id: Int, name: String)
    fun onCourseAction(id: Int, name: String)
}

typealias ArchiveCourseState = Lce<CourseError, List<CourseSummary>>

typealias ArchiveCourseStore = RouterViewModel<CourseArchiveIntent, ArchiveCourseState, DirectEffect, ArchiveRouter>

class ArchiveCourseVM @Inject constructor(
    private val getArchiveCourses: GetArchiveCourses
) : ArchiveCourseStore() {
    override fun createInitialState(): ArchiveCourseState {
        return Lce.Loading
    }

    override fun handleIntents() = intents {
        onNavigate<CourseArchiveIntent.OnBack>(true) { onBack() }
        onNavigate<CourseArchiveIntent.OnMaterial> { onDetail(it.id, it.name) }
        onNavigate<CourseArchiveIntent.OnCourseAction> { onCourseAction(it.id, it.name) }
    }

    init {
        viewModelScope.launch {
            val state = either {
                val result = getArchiveCourses(NoParam).bind()
                Lce.Content(result)
            }.getOrElse {
                Lce.Failure(it)
            }
            setState { state }
        }
    }
}