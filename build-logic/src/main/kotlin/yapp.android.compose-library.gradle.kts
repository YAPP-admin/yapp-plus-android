import com.yapp.plus.buildlogic.COMPILE_SDK_VERSION
import com.yapp.plus.buildlogic.MIN_SDK_VERSION

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk {
        version = release(COMPILE_SDK_VERSION)
    }

    defaultConfig {
        minSdk = MIN_SDK_VERSION
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}
