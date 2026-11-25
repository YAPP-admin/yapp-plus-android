package com.yapp.plus.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface AppNavigationDestination {
    @Serializable
    data object Login : AppNavigationDestination

    @Serializable
    data object SignUp : AppNavigationDestination

    @Serializable
    data object Main : AppNavigationDestination

    @Serializable
    data object AttendanceComplete : AppNavigationDestination

    @Serializable
    data object Notifications : AppNavigationDestination

    @Serializable
    data class Afterparty(
        val id: Long,
    ) : AppNavigationDestination

    @Serializable
    data class Settlement(
        val afterpartyId: Long,
        val settlementId: Long,
    ) : AppNavigationDestination

    @Serializable
    data class SettlementEditor(
        val afterpartyId: Long,
        val settlementId: Long? = null,
    ) : AppNavigationDestination

    @Serializable
    data object MySettlements : AppNavigationDestination

    @Serializable
    data class NotificationPost(
        val postId: Long,
    ) : AppNavigationDestination

    @Serializable
    data class NotificationSchedule(
        val sessionId: Long,
    ) : AppNavigationDestination
}
