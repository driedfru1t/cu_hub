plugins {
    alias(libs.plugins.cuhub.jvm.library)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)
}
