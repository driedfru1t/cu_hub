import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.nikol.cuhub"
    compileSdk {
        version = release(37)
    }
    androidResources {
        generateLocaleConfig = true
        localeFilters += setOf("en", "ru")
    }
    val properties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }
    defaultConfig {
        applicationId = "com.nikol.cuhub"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["YANDEX_CLIENT_ID"] = properties.getProperty("YANDEX_CLIENT_ID")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okHttp)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.datastore.preferences)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.coil.network.ktor3)
    implementation(libs.coil.compose)

    implementation(libs.work.runtime.ktx)

    implementation(projects.core.network)
    implementation(projects.core.di)
    implementation(projects.core.storage)
    implementation(projects.core.security)
    implementation(projects.core.lms.data)
    implementation(projects.core.lms.domain)
    implementation(projects.core.calendar.data)
    implementation(projects.core.calendar.domain)
    implementation(projects.core.prefs)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.ui)

    implementation(projects.feature.auth.authApi)
    implementation(projects.feature.auth.authImpl)
    implementation(projects.feature.lms.lmsApi)
    implementation(projects.feature.lms.lmsImpl)
    implementation(projects.feature.schedule.scheduleImpl)
    implementation(projects.feature.schedule.scheduleApi)
}