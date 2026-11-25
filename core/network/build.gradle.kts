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
            ?: return ""

    return "$apiHost/api/"
}

fun buildConfigString(value: String): String =
    buildString {
        append('"')
        value.forEach { character ->
            when (character) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                else -> append(character)
            }
        }
        append('"')
    }

android {
    namespace = "com.yapp.plus.core.network"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl("dev")}\"")
        }

        getByName("release") {
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl("prod")}\"")
        }
    }
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlinx.coroutines.test)
}
