package com.nikol.lms_impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nikol.lms_impl.viewModels.courseDeatil.CourseDetailVM
import com.nikol.lms_impl.viewModels.CoursesViewModel
import com.nikol.lms_impl.viewModels.MaterialVM
import com.nikol.lms_impl.viewModels.TaskDetailVM
import com.nikol.lms_impl.viewModels.courseDeatil.CourseGradesVM
import com.nikol.lms_impl.viewModels.courseDeatil.CourseMaterialsVM
import com.nikol.viewmodel.DaggerViewModel
import com.nikol.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FactoryVMModule {
    @Binds
    @LmsScope
    fun bindDaggerViewModel(daggerViewModel: DaggerViewModel): ViewModelProvider.Factory
}

@Module
interface CoursesViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CoursesViewModel::class)
    fun bindCoursesViewModel(coursesViewModel: CoursesViewModel): ViewModel
}

@Module
interface CourseDetailVMModule {

    @Binds
    @IntoMap
    @ViewModelKey(CourseDetailVM::class)
    fun bindCourseViewModel(courseViewModel: CourseDetailVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CourseMaterialsVM::class)
    fun bindCourseMaterialsVM(courseViewModel: CourseMaterialsVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CourseGradesVM::class)
    fun bindCourseGradesVM(courseViewModel: CourseGradesVM): ViewModel
}

@Module
interface TaskVMModule {

    @Binds
    @IntoMap
    @ViewModelKey(TaskDetailVM::class)
    fun bindTaskDetailViewModel(taskDetailVM: TaskDetailVM): ViewModel
}

@Module
interface MaterialVMModule {
    @Binds
    @IntoMap
    @ViewModelKey(MaterialVM::class)
    fun bindTaskDetailViewModel(vm: MaterialVM): ViewModel
}

