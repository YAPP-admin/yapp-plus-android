plugins {
    id("yapp.android.compose-library")
    id("yapp.hilt")
}

android {
    namespace = "com.yapp.plus.feature.main.board"
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.markwon.core)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(project(":core:designsystem"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.core)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(project(":core:preview"))
    debugImplementation(libs.androidx.compose.ui.tooling)
}
