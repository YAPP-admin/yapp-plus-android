package com.yapp.plus.feature.main.board.model

import androidx.annotation.StringRes
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.feature.main.board.R

enum class BoardCategory(
    @param:StringRes val labelResource: Int,
) {
    All(R.string.board_category_all),
    Session(R.string.board_category_session),
    Assignment(R.string.board_category_assignment),
    Operation(R.string.board_category_operation),
    ;

    fun toNoticeType(): NoticeType? =
        when (this) {
            All -> null
            Session -> NoticeType.SESSION
            Assignment -> NoticeType.ASSIGNMENT
            Operation -> NoticeType.OPERATION
        }
}
