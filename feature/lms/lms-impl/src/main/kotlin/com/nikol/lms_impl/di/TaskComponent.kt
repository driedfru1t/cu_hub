package com.nikol.lms_impl.di

import com.nikol.di.dep.NetworkCuDep
import com.nikol.viewmodel.FeatureComponent
import dagger.BindsInstance
import dagger.Component

@LmsScope
@Component(
    dependencies = [NetworkCuDep::class],
    modules = [TaskVMModule::class, LmsUseCaseModule::class, FactoryVMModule::class, LmsDataModule::class]
)
interface TaskComponent : FeatureComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance id: Int,
            networkCuDep: NetworkCuDep
        ): TaskComponent
    }
}