plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.chatapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization)


    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp3.urlconnection)

    // Compose Navigation
    implementation(libs.androidx.navigation)

    // Google Auth
    // Newer version then this, may cause some unexpected issues when signing in with
    // One-Tap API, like sudden disappearance of the dialog. 20.7.0 version is the troublesome
    // one right now. Newer version than that one may fix this issue.
    implementation(libs.google.android.gms.play.services.auth)

    // Dagger - Hilt
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.google.dagger.hilt.android.compiler)
    implementation(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation)
    kapt(libs.google.dagger.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
//    kapt "com.google.dagger:hilt-android-compiler:2.48"
//    kapt 'androidx.hilt:hilt-compiler:1.0.0'

    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

    // Coil
    implementation(libs.coil.kt)

    // Kotlinx-DateTime Library
//    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    implementation(libs.kotlinx.datetime)

    //    Paging Library
//    implementation 'androidx.paging:paging-compose:3.2.0'
    implementation(libs.androidx.paging)

    //    Material Icon Extended
//    implementation "androidx.compose.material:material-icons-extended:1.3.0"
    implementation(libs.androidx.compose.material.icon)

//    def ktor_version = "1.6.3"
//    implementation "io.ktor:ktor-client-core:$ktor_version"
//    implementation "io.ktor:ktor-client-cio:$ktor_version"
//    implementation "io.ktor:ktor-client-serialization:$ktor_version"
//    implementation "io.ktor:ktor-client-websockets:$ktor_version"
//    implementation "io.ktor:ktor-client-logging:$ktor_version"
//    implementation "ch.qos.logback:logback-classic:1.2.6"
    implementation(libs.ktor.ch.qos.logback)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.websockets)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")

}