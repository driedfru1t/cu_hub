package com.nikol.di.dep

import com.nikol.common.CuHubDispatcher
import com.nikol.common.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherDep {

    @Dispatcher(CuHubDispatcher.IO)
    fun IODispatcher(): CoroutineDispatcher

    @Dispatcher(CuHubDispatcher.Default)
    fun DefaultDispatcher(): CoroutineDispatcher
}