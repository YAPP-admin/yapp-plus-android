plugins {
    id("yapp.android.library")
}

android {
    namespace = "com.yapp.plus.core.preview"
}

dependencies {
    debugApi(platform(libs.androidx.compose.bom))
    debugApi(libs.androidx.compose.ui.tooling.preview)
}
