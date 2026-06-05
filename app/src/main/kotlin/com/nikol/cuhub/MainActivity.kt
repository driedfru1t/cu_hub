package com.nikol.cuhub

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nikol.auth_api.Auth
import com.nikol.auth_impl.nav.authGraph
import com.nikol.cuhub.app.appComponent
import com.nikol.cuhub.ui.theme.CUHubTheme
import com.nikol.di.ext.LocalAppDep
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val mainComponent = appComponent
        splashScreen.setKeepOnScreenCondition { false }
        setContent {
            var dark by remember { mutableStateOf(true) }
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = getSystemBarStyle(dark),
                    navigationBarStyle = getSystemBarStyle(dark)
                )
                onDispose { }
            }
            CompositionLocalProvider(
                LocalAppDep provides mainComponent
            ) {
                CUHubTheme(dark) {
                    val backStack = rememberNavBackStack(Auth)
                    NavDisplay(
                        backStack = backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            authGraph { dark = !dark }
                        }
                    )
                }
            }
        }
    }

    private fun getSystemBarStyle(isDark: Boolean): SystemBarStyle {
        return if (isDark) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.WHITE)
        }
    }
}
