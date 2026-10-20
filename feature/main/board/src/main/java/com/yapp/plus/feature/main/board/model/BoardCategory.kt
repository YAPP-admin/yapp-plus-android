package com.yapp.plus.feature.main.board.model

import androidx.annotation.StringRes
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.feature.main.board.R

enum class BoardCategory(
    @param:StringRes val labelResource: Int,
    val noticeType: NoticeType? = null,
) {
    All(R.string.board_category_all),
    Operation(R.string.board_category_operation, NoticeType.OPERATION),
    Session(R.string.board_category_session, NoticeType.SESSION),
}
