package com.nikol.lms_impl.viewModels.components

import com.nikol.di.dep.AppDep
import com.nikol.di.ext.ComponentViewModel
import com.nikol.lms_impl.di.CourseComponent
import com.nikol.lms_impl.di.CoursesComponent
import com.nikol.lms_impl.di.DaggerCourseComponent
import com.nikol.lms_impl.di.DaggerCoursesComponent

class CoursesComponentViewModel(appDep: AppDep) : ComponentViewModel<CoursesComponent>(
    component = DaggerCoursesComponent.factory().create(appDep, appDep)
)

class CourseDetailComponentVM(appDep: AppDep, id: Int) : ComponentViewModel<CourseComponent>(
    DaggerCourseComponent.factory().create(id, appDep, appDep)
)