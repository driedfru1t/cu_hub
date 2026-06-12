plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.android.library.compose)
}

android {
    namespace = "com.nikol.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.material3)
}