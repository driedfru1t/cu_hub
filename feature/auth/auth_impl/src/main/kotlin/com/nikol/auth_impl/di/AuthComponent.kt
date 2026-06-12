package com.nikol.auth_impl.di

import androidx.lifecycle.ViewModelProvider
import com.nikol.di.dep.LocalLmsDep
import com.nikol.di.dep.NetworkCuDep
import com.nikol.di.dep.TokenDep
import dagger.Component

@Component(
    dependencies = [TokenDep::class],
    modules = [AuthViewModelModule::class]
)
@AuthScope
interface AuthComponent {

    @Component.Factory
    interface Factory {
        fun create(
            tokenDep: TokenDep
        ): AuthComponent
    }

    fun viewModelFactory(): ViewModelProvider.Factory
}
