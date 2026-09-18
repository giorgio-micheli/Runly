plugins {
    alias(libs.plugins.runly.android.feature.ui)
}

android {
    namespace = "io.github.giorgiomicheli.auth.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}