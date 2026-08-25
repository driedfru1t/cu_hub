package com.nikol.cuhub.di

import com.nikol.lms.backround.DownloadResWorker
import com.nikol.sync.di.ChildWorkerFactory
import com.nikol.sync.di.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(DownloadResWorker::class)
    fun bindMyWorker(factory: DownloadResWorker.Factory): ChildWorkerFactory
}