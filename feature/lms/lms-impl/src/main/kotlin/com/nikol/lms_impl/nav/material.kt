package com.nikol.lms_impl.nav

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.di.ext.rememberComponent
import com.nikol.lms_api.ThemeMaterial
import com.nikol.lms_impl.screens.MaterialScreen
import com.nikol.lms_impl.viewModels.components.MaterialComponentVM
import com.nikol.viewmodel.LocalViewModelFactory

fun EntryProviderScope<NavKey>.material(
    onBack: () -> Unit,
    navigate: (NavKey) -> Unit
) {
    entry<ThemeMaterial> { material ->
        val materialComponent =
            rememberComponent { MaterialComponentVM(it, material.id, material.themeId) }
        CompositionLocalProvider(LocalViewModelFactory provides materialComponent.viewModelFactory()) {
            MaterialScreen(onBack, navigate)
        }
    }
}