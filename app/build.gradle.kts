plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.autozen.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.autozen.app"
        minSdk = 29  // Android Automotive OS minimum
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":feature-dashboard"))
    implementation(project(":feature-trip"))
    implementation(project(":feature-weather"))
    implementation(project(":feature-map"))
    implementation(project(":feature-settings"))
    implementation(project(":core-ui"))
    implementation(project(":core-data"))
    implementation(project(":core-network"))

    implementation(libs.android.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.runtime)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
