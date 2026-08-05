package com.nikol.ui

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

fun getActiveAppLocale(): Locale {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    return appLocales.get(0) ?: Locale.getDefault()
}