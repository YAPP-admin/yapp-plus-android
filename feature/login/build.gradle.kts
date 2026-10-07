plugins {
    id("yapp.android.compose-library")
}

android {
    namespace = "com.yapp.plus.feature.login"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.material3)
    debugImplementation(project(":core:preview"))
    debugImplementation(libs.androidx.compose.ui.tooling)
}
