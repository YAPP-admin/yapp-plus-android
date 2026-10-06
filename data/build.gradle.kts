plugins {
    id("yapp.android.library")
    id("yapp.kotlin.serialization")
}

android {
    namespace = "com.yapp.plus.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:network"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
