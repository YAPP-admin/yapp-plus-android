package com.yapp.plus.feature.main.board.model

import androidx.annotation.StringRes
import com.yapp.plus.feature.main.board.R

enum class BoardCategory(
    @param:StringRes val labelResource: Int,
) {
    All(R.string.board_category_all),
    Operation(R.string.board_category_operation),
    Session(R.string.board_category_session),
}
