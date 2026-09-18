package io.github.giorgiomicheli.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension
) {

    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    commonExtension.run {
        buildFeatures.compose = true
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        "implementation"(platform(bom))
        "implementation"(libs.findLibrary("androidx.compose.ui").get())
        "implementation"(libs.findLibrary("androidx.compose.ui.graphics").get())
        "implementation"(libs.findLibrary("androidx.compose.material3").get())
        "implementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
        "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
    }
}