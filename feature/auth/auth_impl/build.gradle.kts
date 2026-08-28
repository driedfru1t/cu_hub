import java.util.Properties

plugins {
    alias(libs.plugins.cuhub.android.feature.impl)
}

android {
    namespace = "com.nikol.auth_impl"
    val properties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }
    defaultConfig {
        manifestPlaceholders["YANDEX_CLIENT_ID"] = properties.getProperty("YANDEX_CLIENT_ID")
    }
}

dependencies {

    implementation(projects.feature.auth.authApi)
    implementation(projects.core.storage)
    implementation(projects.core.designsystem)
    implementation(projects.core.security)
    implementation(projects.core.prefs)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.yandex.auth.sdk)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mokk)

}