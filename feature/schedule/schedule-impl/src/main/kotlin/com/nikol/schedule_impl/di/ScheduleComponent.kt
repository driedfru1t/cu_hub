package com.nikol.schedule_impl.di

import com.nikol.di.dep.LocalScheduleDep
import com.nikol.di.dep.NetworkYandexDep
import com.nikol.viewmodel.FeatureComponent
import dagger.Component

@Component(
    dependencies = [LocalScheduleDep::class, NetworkYandexDep::class],
    modules = [VMModule::class, ScheduleUCModule::class, ScheduleDataModule::class]
)
@ScheduleScope
interface ScheduleComponent : FeatureComponent {

    @Component.Factory
    interface Factory {
        fun create(
            localScheduleDep: LocalScheduleDep, networkYandexDep: NetworkYandexDep
        ): ScheduleComponent
    }
}
