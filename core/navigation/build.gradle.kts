plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}