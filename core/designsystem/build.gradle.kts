plugins {
    id("yapp.android.compose-library")
}

android {
    namespace = "com.yapp.plus.core.designsystem"
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
