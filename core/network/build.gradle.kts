plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.cuhub.dagger)
}

android {
    namespace = "com.nikol.network"
}

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okHttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.resources)

    implementation(libs.coil.network.ktor3)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(projects.core.security)
}