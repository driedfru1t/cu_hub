package com.nikol.cuhub.app

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.nikol.cuhub.di.AppComponent
import com.nikol.cuhub.di.DaggerAppComponent

class CuHubApplication : Application(), SingletonImageLoader.Factory {
    lateinit var component: AppComponent
    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.factory().create(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return component.imageLoader()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is CuHubApplication -> component
        else -> applicationContext.appComponent
    }