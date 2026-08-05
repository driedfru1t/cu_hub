plugins {
    alias(libs.plugins.cuhub.android.feature.impl)
}

android {
    namespace = "com.nikol.lms_impl"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.feature.lms.lmsApi)
    implementation(projects.core.lms.domain)
    implementation(projects.core.lms.data)
    implementation(projects.core.lms.ui)
    implementation(projects.core.ui)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.arrow.optics)
    implementation(libs.arrow.fx.coroutines)
    ksp(libs.arrow.optics.ksp)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.immutable.collections)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mokk)

    implementation(libs.androidx.material.icons.extended)
}