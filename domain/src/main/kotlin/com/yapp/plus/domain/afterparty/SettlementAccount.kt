package com.yapp.plus.domain.afterparty

data class SettlementAccount(
    val bank: SettlementBank,
    val number: String,
    val holder: String,
) {
    override fun toString(): String = "SettlementAccount(bank=$bank)"
}
