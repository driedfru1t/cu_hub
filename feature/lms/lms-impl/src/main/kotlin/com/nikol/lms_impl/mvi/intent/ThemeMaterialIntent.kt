package com.nikol.lms_impl.mvi.intent

import direct.direct_core.DirectIntent

sealed interface ThemeMaterialIntent : DirectIntent {
    data object Load : ThemeMaterialIntent
}