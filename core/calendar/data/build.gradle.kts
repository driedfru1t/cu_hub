plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.dagger)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.calendar.data"
}

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.work.runtime.ktx)
    implementation(libs.ical4j)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.network)
    implementation(projects.core.cache)
    implementation(projects.core.sync)
    implementation(projects.core.common)
    implementation(projects.core.calendar.domain)

    testImplementation("org.slf4j:slf4j-simple:2.0.17")
}