package com.nikol.auth_impl.screens

import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.nikol.auth_impl.mvi.intent.AuthIntent
import com.nikol.auth_impl.viewModel.AuthRouter
import com.nikol.auth_impl.viewModel.AuthViewModel
import com.nikol.security.CuToken
import com.nikol.viewmodel.daggerViewModel

@Composable
fun CuAuthScreen(
    cuAuthSuccess: () -> Unit
) {
    val authUrl =
        remember { "https://id.centraluniversity.ru/realms/central-university/protocol/openid-connect/auth?response_type=code&client_id=api-gateway&scope=openid+email+offline_access&redirect_uri=https%3A%2F%2Fmy.centraluniversity.ru%2Fapi%2Faccount%2Fsignin%2Fcallback" }
    val targetRedirectDomain = remember { "my.centraluniversity.ru" }


    val vm = daggerViewModel<AuthViewModel, AuthRouter>(key = "cu") { AuthRouter { cuAuthSuccess() } }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                }

                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        if (url != null && url.contains(targetRedirectDomain)) {
                            val cookiesString = cookieManager.getCookie(url)
                            if (!cookiesString.isNullOrEmpty()) {
                                val bffCookieValue = extractCookie(cookiesString, "bff.cookie")
                                    ?: extractCookie(cookiesString, "bff.cockie")

                                if (bffCookieValue != null) {
                                    vm.setIntent(AuthIntent.LogIn(CuToken(bffCookieValue)))
                                }
                            }
                        }
                    }
                }

                loadUrl(authUrl)
            }
        }
    )
}

private fun extractCookie(cookiesString: String, cookieName: String): String? {
    val cookies = cookiesString.split(";")
    for (cookie in cookies) {
        val parts = cookie.split("=", limit = 2)
        if (parts.size == 2 && parts[0].trim() == cookieName) {
            return parts[1].trim()
        }
    }
    return null
}