package com.nikol.lms_impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nikol.lms_impl.viewModels.CoursesViewModel
import com.nikol.viewmodel.DaggerViewModel
import com.nikol.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds

@Module
interface LmsViewModelModule {
    @Binds
    @LmsScope
    fun bindDaggerViewModel(daggerViewModel: DaggerViewModel): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CoursesViewModel::class)
    fun bindCoursesViewModel(coursesViewModel: CoursesViewModel): ViewModel
}