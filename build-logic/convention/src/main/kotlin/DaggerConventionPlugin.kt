import extentions.implementation
import extentions.ksp
import extentions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class DaggerConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.ksp.get().pluginId)
            }

            dependencies {
                implementation(libs.dagger)
                ksp(libs.dagger.compiler)
            }
        }
    }
}