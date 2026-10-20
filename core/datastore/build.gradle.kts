plugins {
    id("yapp.android.library")
    id("yapp.kotlin.serialization")
}

android {
    namespace = "com.yapp.plus.core.datastore"
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
}
