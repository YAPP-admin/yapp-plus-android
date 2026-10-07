package com.yapp.plus

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.auth.LoginContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val barStyle =
            SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
        setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = YappAuthColor.background,
                ) { innerPadding ->
                    LoginContent(
                        onBack = ::finish,
                        onKakaoLogin = {},
                        modifier =
                            Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding),
                    )
                }
            }
        }
    }
}
