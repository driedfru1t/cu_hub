import extentions.api
import extentions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.cuhub.android.library.asProvider().get().pluginId)
                apply(libs.plugins.kotlin.serialization.get().pluginId)
            }
            dependencies {
                api(project(":core:navigation"))
            }
        }
    }
}