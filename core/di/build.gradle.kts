plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.nikol.di"
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
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)

    implementation(projects.core.network)
    implementation(projects.core.storage)
    implementation(projects.core.security)
    implementation(projects.core.lms)
    implementation(projects.core.prefs)

    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

}