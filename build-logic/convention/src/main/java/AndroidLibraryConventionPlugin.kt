import com.android.build.api.dsl.LibraryExtension
import io.github.giorgiomicheli.convention.ExtensionType
import io.github.giorgiomicheli.convention.configureBuildTypes
import io.github.giorgiomicheli.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension>() {
                configureKotlinAndroid(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.LIBRARY
                )

                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            dependencies {
                "testImplementation"(kotlin("test"))
            }
        }
    }
}