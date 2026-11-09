plugins {
    alias(libs.plugins.runly.android.feature.ui)
}

android {
    namespace = "io.github.giorgiomicheli.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
    implementation(libs.junit)
}