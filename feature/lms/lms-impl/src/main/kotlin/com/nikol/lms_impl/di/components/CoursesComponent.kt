package com.nikol.lms_impl.di.components

import com.nikol.di.dep.LocalLmsDep
import com.nikol.di.dep.NetworkCuDep
import com.nikol.lms_impl.di.CourseDetailVMModule
import com.nikol.lms_impl.di.CoursesViewModelModule
import com.nikol.lms_impl.di.FactoryVMModule
import com.nikol.lms_impl.di.LmsDataModule
import com.nikol.lms_impl.di.LmsScope
import com.nikol.lms_impl.di.LmsUseCaseModule
import com.nikol.viewmodel.FeatureComponent
import dagger.BindsInstance
import dagger.Component

@LmsScope
@Component(
    dependencies = [NetworkCuDep::class, LocalLmsDep::class],
    modules = [CoursesViewModelModule::class, LmsDataModule::class, LmsUseCaseModule::class, FactoryVMModule::class]
)
interface CoursesComponent : FeatureComponent {

    @Component.Factory
    interface Factory {
        fun create(
            networkCuDep: NetworkCuDep,
            localLmsDep: LocalLmsDep
        ): CoursesComponent
    }
}

@LmsScope
@Component(
    dependencies = [NetworkCuDep::class, LocalLmsDep::class],
    modules = [CourseDetailVMModule::class, LmsDataModule::class, LmsUseCaseModule::class, FactoryVMModule::class]
)
interface CourseComponent : FeatureComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance id: Int,
            networkCuDep: NetworkCuDep,
            localLmsDep: LocalLmsDep
        ): CourseComponent
    }
}
