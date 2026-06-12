plugins {
    alias(libs.plugins.cuhub.android.feature.impl)
}

android {
    namespace = "com.nikol.lms_impl"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.feature.lms.lmsApi)
    implementation(projects.core.lms)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
}