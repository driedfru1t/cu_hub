package com.nikol.lms_impl.mvi.state

import androidx.compose.runtime.Immutable
import com.nikol.lms.ui.FeedItemUi
import direct.direct_core.DirectState
import kotlinx.collections.immutable.ImmutableCollection

@Immutable
sealed interface ThemeMaterialState : DirectState{
    data object Loading : ThemeMaterialState

    data object Error : ThemeMaterialState

    @Immutable
    data class ThemeMaterialSuccess(
        val list: ImmutableCollection<FeedItemUi>
    ) : ThemeMaterialState
}
