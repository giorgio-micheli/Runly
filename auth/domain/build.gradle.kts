plugins {
    alias(libs.plugins.runly.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
}
