package com.nikol.lms_impl.viewModels.components

import com.nikol.di.dep.AppDep
import com.nikol.di.ext.ComponentViewModel
import com.nikol.lms_impl.di.components.DaggerTaskComponent
import com.nikol.lms_impl.di.components.TaskComponent

class TaskComponentVM(appDep: AppDep) : ComponentViewModel<TaskComponent>(
    component = DaggerTaskComponent.factory().create(appDep, appDep)
)
