package com.nikol.lms_impl.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.di.ext.rememberComponent
import com.nikol.lms.ui.RenderHtmlCompose
import com.nikol.lms_api.ThemeMaterial
import com.nikol.lms_impl.mvi.state.ThemeMaterialState
import com.nikol.lms_impl.viewModels.MaterialVM
import com.nikol.lms_impl.viewModels.components.MaterialComponentVM
import com.nikol.viewmodel.LocalViewModelFactory

fun EntryProviderScope<NavKey>.material() {
    entry<ThemeMaterial> { material ->
        val materialComponent = rememberComponent { MaterialComponentVM(it, material.id) }
        CompositionLocalProvider(LocalViewModelFactory provides materialComponent.viewModelFactory()) {
            val vm = viewModel<MaterialVM>(factory = LocalViewModelFactory.current)
            val state by vm.state.collectAsStateWithLifecycle()

            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                when (val currentState = state) {
                    is ThemeMaterialState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                    }

                    is ThemeMaterialState.Error -> {
                        // Эстетичное отображение ошибки в стиле M3
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error Icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Не удалось загрузить материалы",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Пожалуйста, попробуйте позже.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is ThemeMaterialState.ThemeMaterialSuccess -> {
                        val feedItems = currentState.list.toList()

                        if (feedItems.isEmpty()) {
                            Text(
                                text = "Здесь пока ничего нет",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                    bottom = 24.dp + 80.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    items = feedItems,
                                    key = { it.id }
                                ) { item ->
                                    item.RenderHtmlCompose(

                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}