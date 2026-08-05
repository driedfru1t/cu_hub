package com.nikol.schedule_impl.viewModel.component

import com.nikol.di.dep.AppDep
import com.nikol.di.ext.ComponentViewModel
import com.nikol.schedule_impl.di.DaggerScheduleComponent
import com.nikol.schedule_impl.di.ScheduleComponent

class ScheduleComponentVM(
    appDep: AppDep
) : ComponentViewModel<ScheduleComponent>(
    component = DaggerScheduleComponent.factory().create(appDep, appDep)
)
