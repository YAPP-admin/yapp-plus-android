package com.yapp.plus.domain.afterparty

data class SettlementDraft(
    val account: SettlementAccount,
    val totalAmount: Long,
    val perPersonAmount: Long,
    val recipientIds: Set<Long>,
    val imageIds: List<Long>,
) {
    fun isValid(): Boolean =
        totalAmount > 0 &&
            perPersonAmount > 0 &&
            recipientIds.isNotEmpty() &&
            recipientIds.all { it > 0 } &&
            perPersonAmount <= Long.MAX_VALUE / recipientIds.size &&
            imageIds.size <= MAX_PHOTOS &&
            imageIds.distinct().size == imageIds.size &&
            imageIds.all { it > 0 } &&
            account.number.matches(ACCOUNT_NUMBER) &&
            account.number.any { it in '0'..'9' } &&
            account.holder.codePointCount(0, account.holder.length) in 1..100 &&
            account.holder.none { it == '\u0000' }

    companion object {
        const val MAX_PHOTOS = 5
        const val MAX_PHOTO_BYTES = 10 * 1024 * 1024
        private val ACCOUNT_NUMBER = Regex("[0-9-]{1,50}")
    }
}
