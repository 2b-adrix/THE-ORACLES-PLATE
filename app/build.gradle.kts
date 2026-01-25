
import org.gradle.kotlin.dsl.androidTestImplementation
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.testImplementation
import java.io.FileInputStream
import java.security.KeyStore
import java.security.MessageDigest
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Uncommented this line
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

kapt {
    correctErrorTypes = true
}

android {
    namespace = "com.example.theoraclesplate"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.theoraclesplate"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        // Explicitly set the Kotlin compiler extension version compatible with Kotlin 2.0
        kotlinCompilerExtensionVersion = "1.6.0"
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
    kotlin {
        jvmToolchain(17)

    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.navigation.ui.ktx)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    // Removed duplicate dependency
    // implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Coil
    implementation(libs.coil.compose)
    
    // Cloudinary
    implementation("com.cloudinary:cloudinary-android:3.0.2")

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.androidx.hilt.navigation.compose) 
// Google Maps
    implementation("com.google.maps.android:maps-compose:7.0.0")

}

tasks.register("printSHA1") {
    doLast {
        try {
            val keystoreFile = File(System.getProperty("user.home"), ".android/debug.keystore")
            if (keystoreFile.exists()) {
                val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
                keystore.load(FileInputStream(keystoreFile), "android".toCharArray())
                val cert = keystore.getCertificate("androiddebugkey")
                val digest = MessageDigest.getInstance("SHA-1")
                val hash = digest.digest(cert.encoded)
                val hex = hash.joinToString(":") { "%02X".format(it) }
                File(project.rootDir, "debug_sha1.txt").writeText("SHA1: $hex")
                println("SHA1 Written to debug_sha1.txt")
            } else {
                File(project.rootDir, "debug_sha1.txt").writeText("Error: Debug keystore not found at ${keystoreFile.absolutePath}")
            }
        } catch (e: Exception) {
            File(project.rootDir, "debug_sha1.txt").writeText("Error: ${e.message}")
        }
    }
}
