package com.nikol.lms_impl.mvi.intent

import direct.direct_core.DirectIntent
import direct.direct_core.DirectState

sealed interface TaskDetailIntent : DirectIntent {
    data object InitLoad : TaskDetailIntent
}