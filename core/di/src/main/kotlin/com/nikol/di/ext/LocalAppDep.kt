package com.nikol.di.ext

import androidx.compose.runtime.compositionLocalOf
import com.nikol.di.dep.AppDep

val LocalAppDep = compositionLocalOf<AppDep>{ error("не заинжектен главный компонент") }