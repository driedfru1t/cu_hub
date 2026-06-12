package com.nikol.lms_impl.di

import androidx.lifecycle.ViewModelProvider
import com.nikol.di.dep.LocalLmsDep
import com.nikol.di.dep.NetworkCuDep
import dagger.Component

@LmsScope
@Component(
    dependencies = [NetworkCuDep::class, LocalLmsDep::class],
    modules = [LmsViewModelModule::class, LmsDataModule::class, LmsUseCaseModule::class]
)
interface CoursesComponent {

    @Component.Factory
    interface Factory {
        fun create(
            networkCuDep: NetworkCuDep,
            localLmsDep: LocalLmsDep
        ): CoursesComponent
    }

    fun viewModelProvider(): ViewModelProvider.Factory
}