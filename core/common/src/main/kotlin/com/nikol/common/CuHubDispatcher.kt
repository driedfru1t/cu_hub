package com.nikol.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val cuDispatcher: CuHubDispatcher)

enum class CuHubDispatcher {
    Default,
    IO
}