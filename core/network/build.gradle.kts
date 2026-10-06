plugins {
    id("yapp.android.library")
}

android {
    namespace = "com.yapp.plus.core.network"
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.junit)
}
