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

val debugApiTarget =
    localProperties
        .getProperty("yapp.api.debug.target")
        ?.trim()
        ?.takeIf(String::isNotBlank)
        ?: "dev"

check(debugApiTarget in setOf("dev", "mock")) {
    "yapp.api.debug.target must be either 'dev' or 'mock'"
}

fun mockApiExample(propertyName: String): String =
    if (debugApiTarget == "mock") {
        localProperties.getProperty(propertyName, "").trim()
    } else {
        ""
    }

android {
    namespace = "com.yapp.plus.core.network"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl(debugApiTarget)}\"")
            buildConfigField("Boolean", "MOCK_API_ENABLED", (debugApiTarget == "mock").toString())
            buildConfigField(
                "String",
                "CURRENT_WEEK_MOCK_EXAMPLE",
                buildConfigString(mockApiExample("yapp.api.mock.example.current-week")),
            )
            buildConfigField(
                "String",
                "CURRENT_GENERATION_SESSIONS_MOCK_EXAMPLE",
                buildConfigString(mockApiExample("yapp.api.mock.example.generation-sessions")),
            )
            buildConfigField(
                "String",
                "DEBUG_ACCESS_TOKEN",
                buildConfigString(localProperties.getProperty("yapp.api.debug.token", "").trim()),
            )
        }

        getByName("release") {
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl("prod")}\"")
            buildConfigField("Boolean", "MOCK_API_ENABLED", "false")
            buildConfigField("String", "CURRENT_WEEK_MOCK_EXAMPLE", "\"\"")
            buildConfigField("String", "CURRENT_GENERATION_SESSIONS_MOCK_EXAMPLE", "\"\"")
            buildConfigField("String", "DEBUG_ACCESS_TOKEN", "\"\"")
        }
    }
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.junit)
}
