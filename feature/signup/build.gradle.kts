plugins {
    id("yapp.android.compose-library")
    id("yapp.hilt")
}

android {
    namespace = "com.yapp.plus.feature.signup"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.hilt.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.material3)
    debugImplementation(project(":core:preview"))
    debugImplementation(libs.androidx.compose.ui.tooling)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
}
