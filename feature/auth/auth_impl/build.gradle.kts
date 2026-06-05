plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.auth_impl"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)


    implementation(projects.feature.auth.authApi)
    implementation(projects.core.domain)
    implementation(projects.core.di)
    implementation(projects.core.storage)
    implementation(projects.core.network)
    implementation(projects.core.security)
    implementation(projects.core.prefs)
    implementation(projects.core.viewModel)
    implementation(projects.core.lms)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.direct.viewModel)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)

    implementation(libs.yandex.auth.sdk)
    implementation(libs.androidx.appcompat)

}