package com.nikol.lms_impl.di.components

import com.nikol.di.dep.LocalLmsDep
import com.nikol.di.dep.NetworkCuDep
import com.nikol.lms_impl.di.FactoryVMModule
import com.nikol.lms_impl.di.LmsDataModule
import com.nikol.lms_impl.di.LmsScope
import com.nikol.lms_impl.di.LmsUseCaseModule
import com.nikol.lms_impl.di.TaskVMModule
import com.nikol.lms_impl.viewModels.TaskDetailVM
import com.nikol.viewmodel.FeatureComponent
import dagger.BindsInstance
import dagger.Component

@LmsScope
@Component(
    dependencies = [NetworkCuDep::class, LocalLmsDep::class],
    modules = [TaskVMModule::class, LmsUseCaseModule::class, FactoryVMModule::class, LmsDataModule::class]
)
interface TaskComponent : FeatureComponent {

    @Component.Factory
    interface Factory {
        fun create(
            networkCuDep: NetworkCuDep,
            localLmsDep: LocalLmsDep
        ): TaskComponent
    }

    fun factoryTaskDetailVm(): TaskDetailVM.Factory
}