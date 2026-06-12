package com.nikol.cuhub

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nikol.auth_api.Auth
import com.nikol.auth_impl.nav.authGraph
import com.nikol.cuhub.app.appComponent
import com.nikol.designsystem.theme.CUHubTheme
import com.nikol.di.ext.LocalAppDep
import com.nikol.lms_api.Courses
import com.nikol.lms_impl.nav.lms

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val mainComponent = appComponent
        splashScreen.setKeepOnScreenCondition { false }
        setContent {

            enableEdgeToEdge(
                statusBarStyle = getSystemBarStyle(isSystemInDarkTheme()),
                navigationBarStyle = getSystemBarStyle(isSystemInDarkTheme())
            )

            CompositionLocalProvider(
                LocalAppDep provides mainComponent
            ) {
                CUHubTheme {
                    val backStack = rememberNavBackStack(Auth)
                    NavDisplay(
                        backStack = backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            authGraph {
                                backStack.apply {

                                    add(Courses)
                                }
                            }
                            lms()
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
