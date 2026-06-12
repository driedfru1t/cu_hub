package com.nikol.auth_impl.viewModel

import com.nikol.auth_impl.di.AuthComponent
import com.nikol.auth_impl.di.DaggerAuthComponent
import com.nikol.di.dep.AppDep
import com.nikol.di.ext.ComponentViewModel

class AuthComponentViewModel(
    appComponent: AppDep
) : ComponentViewModel<AuthComponent>(
    component = DaggerAuthComponent.factory().create(appComponent)
)