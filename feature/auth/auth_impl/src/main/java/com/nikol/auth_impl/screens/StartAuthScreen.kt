package com.nikol.auth_impl.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.nikol.auth_impl.R
import com.nikol.auth_impl.viewModel.StartViewModel
import com.nikol.viewmodel.daggerViewModel

@Composable
fun StartAuthScreen(
    onCuAuth: () -> Unit
) {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val currentLocaleTag = appLocales.get(0)?.toLanguageTag() ?: "en"
    val vm = daggerViewModel<StartViewModel>()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.hello))
        Button(
            onClick = onCuAuth
        ) {
            Text("Вход в ЦУ")
        }
        Button(
            onClick = {
                val newLanguageTag = if (currentLocaleTag == "en") "ru" else "en"
                val localeList = LocaleListCompat.forLanguageTags(newLanguageTag)
                AppCompatDelegate.setApplicationLocales(localeList)
            }
        ) {
            Text("Поменять язык")
        }
    }
}