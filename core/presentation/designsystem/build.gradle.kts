plugins {
    alias(libs.plugins.runly.android.library.compose)
}

android {
    namespace = "io.github.giorgiomicheli.core.presentation.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.material3)
}