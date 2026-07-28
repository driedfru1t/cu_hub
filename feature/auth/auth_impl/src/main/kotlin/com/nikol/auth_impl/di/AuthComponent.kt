package com.nikol.auth_impl.di

import com.nikol.di.dep.TokenDep
import com.nikol.viewmodel.FeatureComponent
import dagger.Component

@Component(
    dependencies = [TokenDep::class],
    modules = [AuthViewModelModule::class]
)
@AuthScope
interface AuthComponent : FeatureComponent {

    @Component.Factory
    interface Factory {
        fun create(
            tokenDep: TokenDep
        ): AuthComponent
    }
}
