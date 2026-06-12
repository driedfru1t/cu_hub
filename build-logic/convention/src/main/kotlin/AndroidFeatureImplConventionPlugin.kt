import com.android.build.api.dsl.LibraryExtension
import extentions.implementation
import extentions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureImplConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.cuhub.android.library.asProvider().get().pluginId)
                apply(libs.plugins.cuhub.android.library.compose.get().pluginId)
                apply(libs.plugins.cuhub.dagger.get().pluginId)
                apply(libs.plugins.kotlin.serialization.get().pluginId)
            }
            extensions.configure<LibraryExtension>{
                testOptions.animationsDisabled = true
            }
            dependencies {
                implementation(libs.androidx.compose.runtime)
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.compose.ui.graphics)
                implementation(libs.androidx.compose.material3)

                implementation(project(":core:domain"))
                implementation(project(":core:di"))
                implementation(project(":core:viewModel"))

                implementation(libs.direct.viewModel)

                implementation(platform(libs.arrow.bom))
                implementation(libs.arrow.core)
            }
        }
    }
}