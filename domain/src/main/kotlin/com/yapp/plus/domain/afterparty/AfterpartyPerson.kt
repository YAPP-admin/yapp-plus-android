package com.yapp.plus.domain.afterparty

import com.yapp.plus.domain.profile.UserPosition

data class AfterpartyPerson(
    val recipientId: Long,
    val name: String,
    val teamName: String?,
    val position: UserPosition?,
    val isMe: Boolean,
)
