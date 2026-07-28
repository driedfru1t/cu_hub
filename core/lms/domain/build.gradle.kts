plugins {
    alias(libs.plugins.cuhub.jvm.library)
    alias(libs.plugins.kotlin.serialization)
}
dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.domain)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)
}
