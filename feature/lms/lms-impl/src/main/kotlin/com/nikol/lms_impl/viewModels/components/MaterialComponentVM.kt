package com.nikol.lms_impl.viewModels.components

import com.nikol.di.dep.AppDep
import com.nikol.di.ext.ComponentViewModel
import com.nikol.lms_impl.di.DaggerMaterialComponent
import com.nikol.lms_impl.di.MaterialComponent

class MaterialComponentVM(appDep: AppDep, id: Int) : ComponentViewModel<MaterialComponent>(
    component = DaggerMaterialComponent.factory().create(id, appDep, appDep)
)