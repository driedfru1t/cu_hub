package com.nikol.cuhub.di

import android.content.Context
import coil3.ImageLoader
import com.nikol.cuhub.MainActivity
import com.nikol.cuhub.app.CuHubApplication
import com.nikol.di.dep.AppDep
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class])
interface AppComponent : AppDep {

    fun inject(app: CuHubApplication)

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}