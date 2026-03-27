plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.autozen.dashboard"
    compileSdk = 34
    defaultConfig {
        minSdk = 29
        val weatherKey = project.findProperty("WEATHER_API_KEY")?.toString() ?: ""
        buildConfigField("String", "WEATHER_API_KEY", "\"$weatherKey\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":core-obd"))
    implementation(project(":core-network"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.hilt.navigation.compose)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
