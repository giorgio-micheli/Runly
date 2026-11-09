plugins {
    alias(libs.plugins.runly.android.library)
    alias(libs.plugins.runly.android.room)
}

android {
    namespace = "io.github.giorgiomicheli.analytics.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)

    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
}