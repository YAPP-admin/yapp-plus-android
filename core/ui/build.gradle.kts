plugins {
    id("yapp.android.library")
}

android {
    namespace = "com.yapp.plus.core.ui"
}

dependencies {
    api(project(":domain"))
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
