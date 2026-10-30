package com.yapp.plus.feature.main.my.model

data class MyProfileUiModel(
    val name: String,
    val phoneNumber: String = "",
    val role: String? = null,
    val generation: String? = null,
    val position: String? = null,
)
