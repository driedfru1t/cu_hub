package com.nikol.ui.state

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import direct.direct_core.DirectState

@Immutable
@optics
sealed interface Lce<out E, out A> : DirectState {
    companion object

    object Loading : Lce<Nothing, Nothing>

    @optics
    data class Content<A>(val value: A) : Lce<Nothing, A> {
        companion object
    }

    @optics
    data class Failure<E>(val error: E) : Lce<E, Nothing> {
        companion object
    }
}
