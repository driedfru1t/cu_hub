import com.android.build.api.dsl.ApplicationExtension
import extentions.configureCompose
import extentions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.application.get().pluginId)
                apply(libs.plugins.kotlin.compose.get().pluginId)
            }
            val extension = extensions.getByType<ApplicationExtension>()
            configureCompose(extension)
        }
    }
}