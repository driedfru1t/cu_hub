package com.nikol.cuhub.app

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.nikol.cuhub.di.AppComponent
import com.nikol.cuhub.di.DaggerAppComponent
import com.nikol.sync.di.AppWorkerFactory
import javax.inject.Inject

class CuHubApplication : Application(), SingletonImageLoader.Factory, Configuration.Provider {
    lateinit var component: AppComponent

    @Inject
    lateinit var appWorkerFactory: AppWorkerFactory


    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.factory().create(this)
        component.inject(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return component.imageLoader()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(appWorkerFactory)
            .build()
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is CuHubApplication -> component
        else -> applicationContext.appComponent
    }