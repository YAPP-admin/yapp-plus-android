plugins {
    id("yapp.android.compose-library")
}

android {
    namespace = "com.yapp.plus.core.ui"
}

dependencies {
    api(project(":domain"))
    api(libs.androidx.core.ktx)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
