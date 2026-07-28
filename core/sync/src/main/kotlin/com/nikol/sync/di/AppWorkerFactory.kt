package com.nikol.sync.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.internal.Provider

class AppWorkerFactory (
    private val workerFactories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val entry = workerFactories.entries.find { it.key.name == workerClassName }
        val factoryProvider = entry?.value ?: return null

        return factoryProvider.get().create(appContext, workerParameters)
    }
}