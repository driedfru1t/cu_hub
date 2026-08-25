package com.nikol.lms_impl.mvi.state

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.nikol.lms.backround.DownloadStatus
import com.nikol.lms.data.local.entity.Longread
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.ui.FeedItemUi
import com.nikol.ui.state.Lce
import direct.direct_core.DirectState
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

@Immutable
@optics
sealed interface ThemeMaterialState : DirectState {
    data object Loading : ThemeMaterialState

    data object Error : ThemeMaterialState

    @Immutable
    @optics
    data class ThemeMaterialSuccess(
        val list: ImmutableCollection<FeedItemUi>,
        val files: ImmutableMap<String, DownloadStatus>
    ) : ThemeMaterialState {
        companion object
    }

    companion object
}
