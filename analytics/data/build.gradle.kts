plugins {
    alias(libs.plugins.runly.android.library)
}

android {
    namespace = "io.github.giorgiomicheli.analytics.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
}