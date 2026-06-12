package extentions

import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.implementation(name: Any) {
    add("implementation", name)
}

internal fun DependencyHandlerScope.compileOnly(name: Any) {
    add("compileOnly", name)
}

internal fun DependencyHandlerScope.api(name: Any) {
    add("api", name)
}

internal fun DependencyHandlerScope.ksp(name: Any) {
    add("ksp", name)
}

internal fun DependencyHandlerScope.debugImplementation(name: Any) {
    add("debugImplementation", name)
}

fun DependencyHandlerScope.testImplementation(name: Any) {
    add("testImplementation", name)
}

internal fun DependencyHandlerScope.androidTestImplementation(name: Any) {
    add("androidTestImplementation", name)
}
