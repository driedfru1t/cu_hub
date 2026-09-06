package com.nikol.schedule_impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nikol.schedule_impl.viewModel.EventDetailVM
import com.nikol.schedule_impl.viewModel.ScheduleVM
import com.nikol.viewmodel.DaggerViewModel
import com.nikol.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module(includes = [VMModule::class])
interface ScheduleVMModule {
    @Binds
    @IntoMap
    @ViewModelKey(ScheduleVM::class)
    fun provideScheduleVM(vm: ScheduleVM): ViewModel
}

@Module(includes = [VMModule::class])
interface DetailEventVMModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventDetailVM::class)
    fun bindEventDetailVM(vm: EventDetailVM): ViewModel
}

@Module
interface VMModule {

    @Binds
    @ScheduleScope
    fun bindDaggerViewModel(daggerViewModel: DaggerViewModel): ViewModelProvider.Factory
}