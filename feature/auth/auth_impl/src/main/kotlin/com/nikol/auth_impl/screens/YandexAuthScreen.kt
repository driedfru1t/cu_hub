package com.nikol.auth_impl.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nikol.auth_impl.mvi.intent.AuthIntent
import com.nikol.auth_impl.viewModel.AuthRouter
import com.nikol.auth_impl.viewModel.AuthViewModel
import com.nikol.security.YandexToken
import com.nikol.viewmodel.daggerViewModel
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

@Composable
fun YandexAuthScreen(
    next: () -> Unit
) {
    val vm = daggerViewModel<AuthViewModel, AuthRouter>(key = "yandex") {
        AuthRouter { next() }
    }

    val context = LocalContext.current

    val sdk = remember { YandexAuthSdk.create(YandexAuthOptions(context)) }
    val loginOptions = remember { YandexAuthLoginOptions() }

    val launcher = rememberLauncherForActivityResult(sdk.contract) { result ->
        when (result) {
            is YandexAuthResult.Success -> {
                vm.setIntent(
                    AuthIntent.LogIn(yandexToken = YandexToken(result.token.value))
                )
            }

            is YandexAuthResult.Cancelled,
            is YandexAuthResult.Failure -> Unit
        }
    }


    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Вход через Яндекс",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { launcher.launch(loginOptions) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Войти через Яндекс")
                }
            }
        }
    }
}