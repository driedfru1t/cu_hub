package com.nikol.lms_impl.viewModels

import com.nikol.lms.domain.useCase.GetTaskDetails
import com.nikol.lms.domain.useCase.TaskDetailParam
import com.nikol.lms.ui.MaterialToUiMapper
import com.nikol.lms_impl.mvi.intent.TaskDetailIntent
import direct.direct_core.DirectEffect
import direct.direct_core.DirectState
import direct.direct_core.onSingle
import direct.direct_viewmodel.DirectViewModel
import javax.inject.Inject

class TaskDetailVM @Inject constructor(
    private val id: Int,
    private val materialToUiMapper: MaterialToUiMapper,
    private val getTaskDetails: GetTaskDetails
) : DirectViewModel<TaskDetailIntent, DirectState, DirectEffect>() {
    override fun createInitialState(): DirectState {
        return object : DirectState {}
    }

    override fun handleIntents() = intents {
        onSingle<TaskDetailIntent.InitLoad> {
            getTaskDetails(TaskDetailParam(id))
        }
    }
}