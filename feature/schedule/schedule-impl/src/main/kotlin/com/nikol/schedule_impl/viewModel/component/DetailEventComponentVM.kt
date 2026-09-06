package com.nikol.schedule_impl.viewModel.component

import com.nikol.di.dep.AppDep
import com.nikol.di.ext.ComponentViewModel
import com.nikol.schedule_impl.di.DaggerDetailEventComponent
import com.nikol.schedule_impl.di.DetailEventComponent

class DetailEventComponentVM(
    appDep: AppDep,
    start: Long,
    href: String
) : ComponentViewModel<DetailEventComponent>(
    component = DaggerDetailEventComponent.factory().create(href, start, appDep, appDep)
)