plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    
}

import java.util.Properties
import java.io.FileInputStream

android {
    namespace = "com.creativedesign.webview"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bhorosa"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        buildConfigField("boolean", "ENABLE_FCM", "false")
        buildConfigField("String", "FCM_TOPIC", "\"com.bhorosa\"")
    }

    signingConfigs {
        create("release") {
            val keystorePropsFile = rootProject.file("keystore.properties")
            if (keystorePropsFile.exists()) {
                val keystoreProps = Properties()
                keystoreProps.load(FileInputStream(keystorePropsFile))
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
}
