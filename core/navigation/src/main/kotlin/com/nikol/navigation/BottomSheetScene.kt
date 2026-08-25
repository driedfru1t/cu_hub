package com.nikol.navigation

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

@OptIn(ExperimentalMaterial3Api::class)
data class BottomSheetSceneProperties(
    val sheetValue: SheetValue = SheetValue.Expanded,
    val shape: @Composable () -> Shape = { BottomSheetDefaults.ExpandedShape },
    val containerColor: @Composable () -> Color = { BottomSheetDefaults.ContainerColor },
    val dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    val modalProperties: ModalBottomSheetProperties = ModalBottomSheetProperties()
)

@OptIn(ExperimentalMaterial3Api::class)
data class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val properties: BottomSheetSceneProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val lifecycleOwner = rememberLifecycleOwner()

        val sheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
        )

        ModalBottomSheet(
            onDismissRequest = onBack,
            sheetState = sheetState,
//            properties = properties.modalProperties,
//            shape = properties.shape.invoke(),
//            containerColor = properties.containerColor.invoke(),
//            dragHandle = properties.dragHandle,
        ) {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                entry.Content()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val bottomSheetProperties = lastEntry.metadata[BottomSheetKey] ?: return null
        return bottomSheetProperties.let { properties ->
            @Suppress("UNCHECKED_CAST")
            BottomSheetScene(
                key = lastEntry.contentKey as T,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                entry = lastEntry,
                properties = properties,
                onBack = onBack
            )
        }
    }

    companion object {
        fun bottomSheet(properties: BottomSheetSceneProperties = BottomSheetSceneProperties()) =
            metadata {
                put(BottomSheetKey, properties)
            }

        object BottomSheetKey : NavMetadataKey<BottomSheetSceneProperties>
    }
}