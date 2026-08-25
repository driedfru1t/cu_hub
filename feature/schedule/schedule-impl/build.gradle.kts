plugins {
    alias(libs.plugins.cuhub.android.feature.impl)
}
android {
    namespace = "com.nikol.schedule_impl"
}

dependencies {
    implementation(projects.feature.schedule.scheduleApi)

    implementation(projects.core.network)
    implementation(projects.core.calendar.domain)
    implementation(projects.core.calendar.data)
    implementation(projects.core.ui)
    implementation(projects.core.common)
    implementation(projects.core.storage)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.arrow.optics)
//    implementation(libs.arrow.fx.coroutines)
    ksp(libs.arrow.optics.ksp)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.immutable.collections)

    implementation(libs.androidx.material.icons.extended)
}