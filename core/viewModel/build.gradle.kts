plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.android.library.compose)
    alias(libs.plugins.cuhub.dagger)
}

android {
    namespace = "com.nikol.viewmodel"
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.direct.viewModel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.graphics)

    implementation(projects.core.di)
}