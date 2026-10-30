package com.yapp.plus.data.notice

import java.net.URI

internal fun validatedNoticeWebUrl(value: String): String {
    val uri = URI(value)
    require(uri.scheme in setOf("https", "http") && !uri.host.isNullOrBlank())
    return value
}
