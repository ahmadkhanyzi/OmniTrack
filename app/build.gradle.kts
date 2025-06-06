plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.firebase) // ✅ Correctly applied Firebase Plugin
}

android {
    namespace = "com.example.omnitrack"
    compileSdk = 35 // Consider using the latest stable SDK, e.g., 34, unless you specifically need 35 (which might be a preview)

    defaultConfig {
        applicationId = "com.example.omnitrack"
        minSdk = 24
        targetSdk = 35 // Match compileSdk, or use the latest stable
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // vectorDrawables {
        // useSupportLibrary = true // Consider adding this if you use vector drawables extensively for backward compatibility
        // }
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Consider enabling minify for release builds (isMinifyEnabled = true) to reduce app size
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Or consider VERSION_17 or higher if your project allows
        targetCompatibility = JavaVersion.VERSION_11 // Or consider VERSION_17 or higher
    }
    kotlinOptions {
        jvmTarget = "11" // Match Java compatibility versions
    }
    buildFeatures {
        compose = true
    }
    // It's good practice to also declare the compose compiler version if not managed by AGP/Kotlin plugin compatibility
    // composeOptions {
    //     kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() // Assuming you define composeCompiler in libs.versions.toml
    // }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose Bill of Materials (BOM) - Manages versions for Compose libraries
    implementation(platform(libs.androidx.compose.bom)) // ✅ Ensure this is present if you didn't explicitly show it before
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview) // This is for previews in Android Studio
    implementation(libs.androidx.material3)

    // Firebase Bill of Materials (BOM) - Manages versions for Firebase libraries
    implementation(platform(libs.firebaseBom))      // ✅ Correct Firebase BOM reference
    implementation(libs.firebase.auth)              // ✅ Firebase Authentication (version managed by BOM)
    implementation(libs.firebase.database)          // ✅ Firebase Realtime Database (version managed by BOM)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // For Compose testing
    androidTestImplementation(libs.androidx.ui.test.junit4)        // For Compose UI tests

    // Debugging
    debugImplementation(libs.androidx.ui.tooling) // This provides tools like Layout Inspector for Compose
    debugImplementation(libs.androidx.ui.test.manifest)

    // Manual dependencies (Consider moving these to your version catalog - libs.versions.toml)
    implementation("androidx.appcompat:appcompat:1.6.1") // Often not strictly needed in a pure Compose app, but might be for specific themes/components or if you have XML layouts
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.material:material:1.10.0") // Similar to appcompat, this is for Material Design Components for XML views. If purely Compose, this might not be needed or could be replaced by Compose Material 3.
    implementation("com.facebook.android:facebook-android-sdk:18.0.3")
    implementation("com.google.android.gms:play-services-auth:20.6.0")

}