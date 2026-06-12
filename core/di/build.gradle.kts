plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.android.library.compose)
    alias(libs.plugins.cuhub.dagger)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.di"
}

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(libs.androidx.compose.runtime)

    implementation(projects.core.network)
    implementation(projects.core.storage)
    implementation(projects.core.security)
    implementation(projects.core.lms)
    implementation(projects.core.prefs)

    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

}