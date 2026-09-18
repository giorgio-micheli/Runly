plugins {
    alias(libs.plugins.runly.android.library)
    alias(libs.plugins.runly.jvm.ktor)
}

android {
    namespace = "io.github.giorgiomicheli.run.network"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
}