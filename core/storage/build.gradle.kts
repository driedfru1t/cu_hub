plugins {
    alias(libs.plugins.cuhub.android.library)
    alias(libs.plugins.cuhub.dagger)
    alias(libs.plugins.cuhub.android.room)
}

android {
    namespace = "com.nikol.storage"
}

dependencies {
    implementation(projects.core.lms.data)
    implementation(projects.core.lms.domain)
    implementation(projects.core.calendar.data)
    implementation(projects.core.calendar.domain)
}