plugins {
    alias(libs.plugins.cuhub.android.library)
}

android {
    namespace = "com.nikol.cache"
}

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)

    implementation(projects.core.network)
}