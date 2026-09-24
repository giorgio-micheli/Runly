plugins {
    alias(libs.plugins.runly.android.library)
    alias(libs.plugins.runly.jvm.ktor)
}

android {
    namespace = "io.github.giorgiomicheli.auth.data"
}

dependencies {
    implementation(libs.bundles.koin)

    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}