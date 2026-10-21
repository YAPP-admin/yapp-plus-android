package com.yapp.plus.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface AppNavigationDestination {
    @Serializable
    data object Login : AppNavigationDestination

    @Serializable
    data object Name : AppNavigationDestination

    @Serializable
    data object Phone : AppNavigationDestination

    @Serializable
    data object SignUpComplete : AppNavigationDestination

    @Serializable
    data object Main : AppNavigationDestination

    @Serializable
    data object AttendanceComplete : AppNavigationDestination
}
