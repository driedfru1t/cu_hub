import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.nikol.build-logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(gradleApi())
    implementation(files((libs).javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.plugin.android.application)
    implementation(libs.plugin.kotlin.compose)
    implementation(libs.plugin.ksp)
    implementation(libs.plugin.kotlin.serialisation)
    implementation(libs.plugin.android.library)
    implementation(libs.plugin.jetbrains.kotlin.jvm)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = libs.plugins.cuhub.android.application.compose.get().pluginId
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.cuhub.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.cuhub.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.cuhub.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("dagger") {
            id = libs.plugins.cuhub.dagger.get().pluginId
            implementationClass = "DaggerConventionPlugin"
        }
        register("androidFeatureImpl") {
            id = libs.plugins.cuhub.android.feature.impl.get().pluginId
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }
        register("androidFeatureApi") {
            id = libs.plugins.cuhub.android.feature.api.get().pluginId
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }
        register("jvmLibrary"){
            id = libs.plugins.cuhub.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}