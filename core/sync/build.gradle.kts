plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.dagger)
}

android {
    namespace = "com.nikol.sync"
}

dependencies {
    implementation(libs.work.runtime.ktx)
}