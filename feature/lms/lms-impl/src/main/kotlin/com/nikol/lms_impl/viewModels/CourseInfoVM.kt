package com.nikol.lms_impl.viewModels

import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.DirectIntent
import direct.direct_core.DirectState

fun interface CourseInfoRouter : Router {
    fun onBack()
}

typealias CourseInfoStore = RouterViewModel<DirectIntent, DirectState, DirectEffect, CourseInfoRouter>

class CourseInfoVM : CourseInfoStore() {
    override fun createInitialState(): DirectState {
        TODO()
    }

    override fun handleIntents() {
        TODO("Not yet implemented")
    }
}