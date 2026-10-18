import com.yapp.plus.buildlogic.COMPILE_SDK_VERSION
import com.yapp.plus.buildlogic.MIN_SDK_VERSION
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("yapp.ktlint")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libsCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

android {
    compileSdk {
        version = release(COMPILE_SDK_VERSION)
    }

    defaultConfig {
        minSdk = MIN_SDK_VERSION
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    coreLibraryDesugaring(libsCatalog.findLibrary("desugar-jdk-libs").get())
}
