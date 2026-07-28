package com.nikol.ui.state

import androidx.compose.runtime.Immutable
import direct.direct_core.DirectState

@Immutable
sealed interface Lce<out E, out A> : DirectState {
    object Loading : Lce<Nothing, Nothing>
    data class Content<A>(val value: A) : Lce<Nothing, A>
    data class Failure<E>(val error: E) : Lce<E, Nothing>
}
