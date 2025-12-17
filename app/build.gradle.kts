import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)

}

android {
    namespace = "com.example.presentation"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.stripesdemo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"


        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                load(file.inputStream())
            }
        }

        val scanditLicenseKey: String =
            localProperties.getProperty("SCANDIT_LICENSE_KEY") ?: ""

        buildConfigField(
            "String",
            "SCANDIT_LICENSE_KEY",
            "\"$scanditLicenseKey\""
        )


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }

    hilt {
        enableAggregatingTask = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.appcompat)


    // material 3
    implementation(libs.material3)
    implementation(libs.compose.material.icons)


    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines + Flows
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Casio Device
    implementation(files("src/main/libs/CasioDeviceLibrary.aar"))

    // Mobile Scan
    implementation(libs.codescanner)
    implementation(libs.gpsCoroutines)

    // Database (Room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.room.testing)

    // Gson
    implementation(libs.gson)

    // Qr code
    implementation(libs.zxing.core)
    implementation(libs.zxing.javase)

    // Serialization
    implementation(libs.serialization)

    // lifecycle
    implementation(libs.lifecycle.process)

    api("com.scandit.datacapture:core:8.0.0")
    api("com.scandit.datacapture:barcode:8.0.0")

}