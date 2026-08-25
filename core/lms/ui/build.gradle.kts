plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.android.library.compose)
    alias(libs.plugins.cuhub.dagger)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.lms.ui"
}

dependencies {
    implementation(libs.androidx.material3)

    implementation(libs.immutable.collections)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    implementation(projects.core.lms.domain)
    implementation(projects.core.lms.backroundWork)
    implementation(projects.core.ui)
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.jsoup)
}