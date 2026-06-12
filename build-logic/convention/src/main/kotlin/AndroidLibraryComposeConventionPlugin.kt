import com.android.build.api.dsl.LibraryExtension
import extentions.configureCompose
import extentions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.library.get().pluginId)
                apply(libs.plugins.kotlin.compose.get().pluginId)
            }
            configureCompose(extensions.getByType<LibraryExtension>())
        }
    }
}