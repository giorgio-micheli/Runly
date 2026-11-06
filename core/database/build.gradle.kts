plugins {
    alias(libs.plugins.runly.android.library)
    alias(libs.plugins.runly.android.room)
}

android {
    namespace = "io.github.giorgiomicheli.core.database"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.bundles.koin)
    implementation(libs.org.mongodb.bson)
}