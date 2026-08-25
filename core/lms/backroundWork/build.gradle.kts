plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.dagger)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.lms.backround"
}

dependencies {
    implementation(projects.core.lms.data)
    implementation(projects.core.sync)
    implementation(projects.core.network)
    implementation(projects.core.common)

    implementation(libs.work.runtime.ktx)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

}