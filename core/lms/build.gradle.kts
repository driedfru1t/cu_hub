plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.dagger)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.lms"
}

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)


    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)


    implementation(projects.core.network)
    implementation(projects.core.cache)
    implementation(projects.core.domain)
}