package com.nikol.schedule_impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nikol.schedule_impl.viewModel.ScheduleVM
import com.nikol.viewmodel.DaggerViewModel
import com.nikol.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface VMModule {

    @Binds
    @ScheduleScope
    fun bindDaggerViewModel(daggerViewModel: DaggerViewModel): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ScheduleVM::class)
    fun provideScheduleVM(vm: ScheduleVM): ViewModel
}