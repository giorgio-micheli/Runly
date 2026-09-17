plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.giorgiomicheli.auth.data"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}