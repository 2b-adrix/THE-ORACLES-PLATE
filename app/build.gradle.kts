import org.gradle.kotlin.dsl.androidTestImplementation
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.testImplementation
import java.io.FileInputStream
import java.security.KeyStore
import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.theoraclesplate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.theoraclesplate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.denzcoskun:ImageSlideshow:0.0.7")
    
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
