plugins {
    alias(libs.plugins.runly.android.feature.ui)
}

android {
    namespace = "io.github.giorgiomicheli.run.presentation"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.run.domain)
}