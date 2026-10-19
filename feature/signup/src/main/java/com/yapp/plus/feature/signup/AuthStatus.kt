package com.yapp.plus.feature.signup

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yapp.plus.core.designsystem.R as DesignSystemR

enum class AuthStatus(
    @param:StringRes val titleResource: Int,
    @param:StringRes val descriptionResource: Int,
    @param:DrawableRes val graphicResource: Int,
) {
    Pending(
        titleResource = R.string.auth_pending_title,
        descriptionResource = R.string.auth_pending_description,
        graphicResource = DesignSystemR.drawable.graphic_point_construction,
    ),
    Complete(
        titleResource = R.string.auth_complete_title,
        descriptionResource = R.string.auth_complete_description,
        graphicResource = DesignSystemR.drawable.graphic_point_happy,
    ),
    Problem(
        titleResource = R.string.auth_problem_title,
        descriptionResource = R.string.auth_problem_description,
        graphicResource = DesignSystemR.drawable.graphic_point_disabled,
    ),
}
