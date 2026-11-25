package com.yapp.plus.feature.main.community

internal class SettlementPhotoInput(
    val bytes: ByteArray,
    val contentType: String,
) {
    val filename: String get() = if (contentType == "image/png") "receipt.png" else "receipt.jpg"
}
