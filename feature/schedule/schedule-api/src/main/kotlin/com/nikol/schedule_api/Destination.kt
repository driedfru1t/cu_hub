package com.nikol.schedule_api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data object Schedule : NavKey

@Serializable
data class EventDetail(
    val href: String,
    val start: Long
) : NavKey