package com.yapp.plus.domain.auth

enum class MembershipReviewStatus(
    val value: String,
) {
    Approved("APPROVED"),
    Pending("PENDING"),
    Rejected("REJECTED"),
    Unknown("UNKNOWN");

    companion object {
        fun fromValue(value: String?): MembershipReviewStatus =
            entries.firstOrNull { it.value == value } ?: Unknown
    }
}
