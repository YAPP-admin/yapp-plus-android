package com.yapp.plus.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface AuthDemoDestination {
    @Serializable
    data object Login : AuthDemoDestination

    @Serializable
    data object Name : AuthDemoDestination

    @Serializable
    data object Phone : AuthDemoDestination

    @Serializable
    data object SignUpComplete : AuthDemoDestination

    @Serializable
    data object Main : AuthDemoDestination

    @Serializable
    data object AttendanceComplete : AuthDemoDestination
}
