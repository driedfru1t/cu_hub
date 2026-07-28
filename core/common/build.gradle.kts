plugins {
    alias(libs.plugins.cuhub.jvm.library)
    alias(libs.plugins.cuhub.dagger)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}