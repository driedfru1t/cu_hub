package com.nikol.cuhub

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TransformOrigin
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import com.nikol.auth_api.Auth
import com.nikol.auth_impl.nav.authGraph
import com.nikol.cuhub.app.appComponent
import com.nikol.cuhub.nav.mainGraph
import com.nikol.cuhub.vm.ConfigState
import com.nikol.cuhub.vm.InitVM
import com.nikol.designsystem.theme.CUHubTheme
import com.nikol.di.ext.LocalAppDep
import com.nikol.lms_impl.nav.material
import com.nikol.navigation.Main
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: InitVM.Factory

    val vm by viewModels<InitVM> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { vm.state.value.shouldKeepSplashScreen }

        setContent {
            val isDark = isSystemInDarkTheme()
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                    ) { isDark },

                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b),
                    ) { isDark }
                )
            }

            CompositionLocalProvider(
                LocalAppDep provides appComponent
            ) {
                CUHubTheme {
                    val auth by vm.state.collectAsStateWithLifecycle()
                    if (auth is ConfigState.Success) {
                        val backStack = rememberNavBackStack(if (auth.isAuthSuccess) Main else Auth)
                        NavDisplay(
                            backStack = backStack,
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            popTransitionSpec = {
                                EnterTransition.None togetherWith slideOutHorizontally(
                                    targetOffsetX = { width -> width },
                                    animationSpec = tween(300)
                                )
                            },
                            predictivePopTransitionSpec = { swipeEdge ->
                                val directionMultiplier = when (swipeEdge) {
                                    NavigationEvent.EDGE_LEFT -> 1f
                                    NavigationEvent.EDGE_RIGHT -> -1f
                                    else -> 0f
                                }

                                EnterTransition.None togetherWith (
                                        slideOutHorizontally(
                                            targetOffsetX = { width -> (width * 0.08f * directionMultiplier).toInt() },
                                            animationSpec = tween(300)
                                        ) + scaleOut(
                                            targetScale = 0.9f,
                                            transformOrigin = TransformOrigin(0.5f, 0.5f),
                                            animationSpec = tween(300)
                                        )
                                        )
                            },
                            entryProvider = entryProvider {
                                authGraph {
                                    backStack.apply {
                                        clear()
                                        add(Main)
                                    }
                                }
                                mainGraph(
                                    onBackRoot = { backStack.removeLastOrNull() },
                                    navigateToRoot = { backStack.add(it) }
                                )
                                material(
                                    onBack = { backStack.removeLastOrNull() },
                                    navigate = { backStack.add(it) }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
