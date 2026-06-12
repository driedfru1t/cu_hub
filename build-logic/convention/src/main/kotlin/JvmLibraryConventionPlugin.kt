import extentions.configureKotlinJvm
import extentions.libs
import extentions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.jetbrains.kotlin.jvm.get().pluginId)
            }
            configureKotlinJvm()
            dependencies {
                testImplementation(libs.junit)
            }
        }
    }
}