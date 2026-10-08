package com.yapp.plus.feature.attendance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yapp.plus.core.designsystem.theme.YappMainColor

@Composable
fun AttendanceContent(
    modifier: Modifier = Modifier,
    initialTab: AttendanceTab = AttendanceTab.Home,
) {
    var selectedTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = YappMainColor.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AttendanceBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
    ) { contentPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding),
        ) {
            when (selectedTab) {
                AttendanceTab.Home -> AttendanceHomeScreen()
                AttendanceTab.Schedule -> AttendanceScheduleScreen()
                AttendanceTab.Board -> AttendanceBoardScreen()
                AttendanceTab.My -> AttendanceMyScreen()
            }
        }
    }
}
