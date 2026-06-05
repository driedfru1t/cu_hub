package com.nikol.auth_impl.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Start : NavKey

@Serializable
data object CuAuth : NavKey

@Serializable
data object YandexAuth : NavKey