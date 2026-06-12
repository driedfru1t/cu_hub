import com.android.build.api.dsl.LibraryExtension
import extentions.androidTestImplementation
import extentions.configureKotlinAndroid
import extentions.libs
import extentions.testImplementation
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies


class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.library.get().pluginId)
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.animationsDisabled = true
                defaultConfig.apply {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }

            dependencies {
                testImplementation(libs.junit)
                androidTestImplementation(libs.androidx.junit)
            }
        }
    }
}