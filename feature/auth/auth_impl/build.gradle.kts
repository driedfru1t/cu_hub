plugins {
    alias(libs.plugins.cuhub.android.feature.impl)
}

android {
    namespace = "com.nikol.auth_impl"
}

dependencies {

    implementation(projects.feature.auth.authApi)
    implementation(projects.core.storage)
    implementation(projects.core.security)
    implementation(projects.core.prefs)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.yandex.auth.sdk)
    implementation(libs.androidx.appcompat)

}