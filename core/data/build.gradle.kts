plugins {
    alias(libs.plugins.runly.android.library)
    alias(libs.plugins.runly.jvm.ktor)
}

android {
    namespace = "io.github.giorgiomicheli.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)

    implementation(libs.timber)
}