package com.nikol.cuhub.di

import android.content.Context
import com.nikol.di.dep.AppDep
import com.nikol.di.dep.NetworkDep
import com.nikol.di.dep.StorageDep
import com.nikol.di.dep.TokenDep
import com.nikol.security.TokenManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class])
interface AppComponent : AppDep {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}