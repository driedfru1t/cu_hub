package com.nikol.cuhub.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.bottomsheet.BottomSheetSceneStrategy
import com.nikol.lms_api.ArchiveCourses
import com.nikol.lms_api.Courses
import com.nikol.lms_impl.nav.lms
import com.nikol.navigation.Main
import com.nikol.navigation.Navigator
import com.nikol.navigation.rememberNavigationState
import com.nikol.schedule_api.Schedule
import com.nikol.schedule_impl.nav.schedule
import com.nikol.ui.R
import kotlinx.serialization.Serializable

data class NavBarItem(
    val icon: Int,
)

@Serializable
data object RouteB : NavKey

@Serializable
data object RouteC : NavKey

val TOP_LEVEL_ROUTES = mapOf(
    Courses to NavBarItem(icon = R.drawable.school),
    Schedule to NavBarItem(R.drawable.science),
    RouteB to NavBarItem(R.drawable.design_services)
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun EntryProviderScope<NavKey>.mainGraph() {
    entry<Main> {
        val navigationState = rememberNavigationState(Courses, TOP_LEVEL_ROUTES.keys)
        val navigator = remember { Navigator(navigationState) }
        val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

        val entryProvider = entryProvider {
            lms(
                onBack = { navigator.onBack() },
                navigateTo = { navigator.navigate(it) }
            )
            schedule()
            entry<RouteB> {
                Scaffold { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("B")
                    }
                }
            }
            entry<RouteC> {
                Scaffold { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("C")
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            NavDisplay(
                entries = navigationState.toDecoratedEntries(entryProvider),
                onBack = { navigator.onBack() },
                sceneStrategies = listOf(bottomSheetStrategy)
            )

            HorizontalFloatingToolbar(
                expanded = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .systemBarsPadding()
                    .padding(bottom = 4.dp),
                colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                    toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                )
            ) {
                TOP_LEVEL_ROUTES.forEach { (route, navItem) ->
                    val isSelected = navigationState.topLevelRoute == route

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(width = 64.dp, height = 36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable {
                                navigator.navigate(route)
                            }
                    ) {
                        Icon(
                            painter = painterResource(navItem.icon),
                            contentDescription = null,
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}