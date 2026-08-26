package com.nikol.lms_impl.di.components

import com.nikol.di.dep.LocalLmsDep
import com.nikol.di.dep.NetworkCuDep
import com.nikol.lms_impl.di.FactoryVMModule
import com.nikol.lms_impl.di.LmsDataModule
import com.nikol.lms_impl.di.LmsScope
import com.nikol.lms_impl.di.LmsUseCaseModule
import com.nikol.lms_impl.di.MaterialVMModule
import com.nikol.lms_impl.di.qualifire.MaterialId
import com.nikol.lms_impl.di.qualifire.ThemeId
import com.nikol.viewmodel.FeatureComponent
import dagger.BindsInstance
import dagger.Component

@LmsScope
@Component(
    dependencies = [NetworkCuDep::class, LocalLmsDep::class],
    modules = [MaterialVMModule::class, LmsUseCaseModule::class, FactoryVMModule::class, LmsDataModule::class]
)
interface MaterialComponent : FeatureComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance @MaterialId id: Int,
            @BindsInstance @ThemeId themeId: Int,
            networkCuDep: NetworkCuDep,
            localLmsDep: LocalLmsDep
        ): MaterialComponent
    }
}