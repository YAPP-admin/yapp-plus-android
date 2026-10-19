package com.yapp.plus

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.navigation.AppStartRoute
import com.yapp.plus.navigation.AuthDemoDestination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val barStyle =
            SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
        setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                AppStartRoute(
                    onExit = ::finish,
                    startDestination =
                        if (BuildConfig.DEBUG) {
                            AuthDemoDestination.Main
                        } else {
                            AuthDemoDestination.Login
                        },
                )
            }
        }
    }
}
