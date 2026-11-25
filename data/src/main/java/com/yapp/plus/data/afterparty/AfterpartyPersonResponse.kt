package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.profile.UserPosition
import kotlinx.serialization.Serializable

@Serializable
internal data class AfterpartyPersonResponse(
    val recipientId: Long,
    val name: String,
    val teamName: String?,
    val position: String?,
    val isMe: Boolean,
) {
    fun toDomain(): AfterpartyPerson =
        AfterpartyPerson(
            recipientId,
            name,
            teamName,
            position?.let { code -> UserPosition.entries.firstOrNull { it.name == code } ?: UserPosition.UNKNOWN },
            isMe,
        )
}
