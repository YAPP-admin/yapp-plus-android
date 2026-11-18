package com.yapp.plus.domain.session

sealed interface SessionDistance {
    data class Measured(
        val meters: Int,
        val isApproximate: Boolean,
    ) : SessionDistance

    data object PermissionRequired : SessionDistance

    data object LocationDisabled : SessionDistance

    data object Unavailable : SessionDistance
}
