package com.nikol.lms_impl.viewModels

import com.nikol.di.dep.AppDep
import com.nikol.di.ext.ComponentViewModel
import com.nikol.lms_impl.di.CoursesComponent
import com.nikol.lms_impl.di.DaggerCoursesComponent

class CoursesComponentViewModel(appDep: AppDep) : ComponentViewModel<CoursesComponent>(
    component = DaggerCoursesComponent.factory().create(appDep, appDep)
)