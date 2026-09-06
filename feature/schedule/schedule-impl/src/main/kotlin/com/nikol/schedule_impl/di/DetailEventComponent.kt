package com.nikol.schedule_impl.di

import com.nikol.di.dep.LocalScheduleDep
import com.nikol.di.dep.NetworkYandexDep
import com.nikol.viewmodel.FeatureComponent
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [LocalScheduleDep::class, NetworkYandexDep::class],
    modules = [DetailEventVMModule::class, ScheduleUCModule::class, ScheduleDataModule::class]
)
@ScheduleScope
interface DetailEventComponent : FeatureComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance href: String,
            @BindsInstance start: Long,
            localScheduleDep: LocalScheduleDep,
            networkYandexDep: NetworkYandexDep
        ): DetailEventComponent
    }
}