pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CU_Hub"
include(":app")
include(":core:network")
include(":core:di")
include(":core:storage")
include(":core:security")
include(":feature:auth:auth_api")
include(":feature:auth:auth_impl")
include(":core:domain")
include(":core:viewModel")
include(":core:navigation")
include(":core:lms")
include(":core:cache")
include(":core:prefs")
include(":feature:lms:lms-impl")
include(":feature:lms:lms-api")
include(":core:ui")
include(":core:designsystem")
