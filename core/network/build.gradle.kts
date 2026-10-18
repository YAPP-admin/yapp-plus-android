import java.util.Properties

plugins {
    id("yapp.android.library")
}

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { load(it) }
        }
    }

fun apiBaseUrl(environment: String): String {
    val apiHost =
        localProperties
            .getProperty("yapp.api.$environment.host")
            ?.trimEnd('/')
            ?.takeIf(String::isNotBlank)
            ?: if (environment == "mock") {
                error("Set yapp.api.mock.host in local.properties to use the Mock API")
            } else {
                return ""
            }

    return "$apiHost/api/"
}

val debugApiTarget =
    localProperties
        .getProperty("yapp.api.debug.target")
        ?.trim()
        ?.takeIf(String::isNotBlank)
        ?: "dev"

check(debugApiTarget in setOf("dev", "mock")) {
    "yapp.api.debug.target must be either 'dev' or 'mock'"
}

android {
    namespace = "com.yapp.plus.core.network"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl(debugApiTarget)}\"")
        }

        getByName("release") {
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl("prod")}\"")
        }
    }
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.junit)
}
