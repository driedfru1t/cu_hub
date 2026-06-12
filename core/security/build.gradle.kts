plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.dagger)
}

android {
    namespace = "com.nikol.security"
}

dependencies {
    implementation(libs.datastore.preferences)

    implementation(projects.core.prefs)
}