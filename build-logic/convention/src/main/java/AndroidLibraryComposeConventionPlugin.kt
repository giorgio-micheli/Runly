import com.android.build.api.dsl.LibraryExtension
import io.github.giorgiomicheli.convention.ExtensionType
import io.github.giorgiomicheli.convention.configureAndroidCompose
import io.github.giorgiomicheli.convention.configureBuildTypes
import io.github.giorgiomicheli.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("runly.android.library")
            }

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}