plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.android.library.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.nikol.ui"
}

dependencies {
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.direct.core)
    implementation(projects.core.lms.domain)
    implementation(projects.core.designsystem)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.optics)
    ksp(libs.arrow.optics.ksp)
}
