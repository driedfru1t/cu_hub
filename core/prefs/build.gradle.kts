plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.dagger)
}

android {
    namespace = "com.nikol.prefs"
}

dependencies {
    implementation(libs.datastore.preferences)
}