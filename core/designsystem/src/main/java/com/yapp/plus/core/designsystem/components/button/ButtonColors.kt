package com.yapp.plus.core.designsystem.components.button

import androidx.compose.ui.graphics.Color
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappHomeColor

private const val DISABLED_BRAND_ALPHA = 0.5f

internal data class ButtonColors(
    val container: Color,
    val content: Color,
    val border: Color?,
    val alpha: Float = 1f,
) {
    companion object {
        fun from(
            variant: YappButtonVariant,
            enabled: Boolean,
        ): ButtonColors =
            when (variant) {
                YappButtonVariant.SolidNeutral ->
                    ButtonColors(
                        container =
                            if (enabled) {
                                YappHomeColor.action
                            } else {
                                YappHomeColor.disabledAction
                            },
                        content = YappColor.white,
                        border = null,
                    )
                YappButtonVariant.SolidBrand ->
                    ButtonColors(
                        container = YappColor.brand,
                        content = YappColor.white,
                        border = null,
                        alpha = if (enabled) 1f else DISABLED_BRAND_ALPHA,
                    )

                YappButtonVariant.Kakao ->
                    ButtonColors(
                        container = YappColor.kakaoContainer,
                        content = YappColor.foregroundNeutral,
                        border = null,
                        alpha = if (enabled) 1f else DISABLED_BRAND_ALPHA,
                    )

                YappButtonVariant.SolidPrimary ->
                    ButtonColors(
                        container =
                            if (enabled) {
                                YappColor.primary
                            } else {
                                YappColor.coolNeutralWeak
                            },
                        content =
                            if (enabled) {
                                YappColor.white
                            } else {
                                YappColor.contentDisabled
                            },
                        border = null,
                    )

                YappButtonVariant.SolidSecondary ->
                    ButtonColors(
                        container =
                            if (enabled) {
                                YappColor.orangeWeak
                            } else {
                                YappColor.disabledSecondaryContainer
                            },
                        content =
                            if (enabled) {
                                YappColor.primary
                            } else {
                                YappColor.disabledSecondaryContent
                            },
                        border = null,
                    )

                YappButtonVariant.OutlinedPrimary ->
                    ButtonColors(
                        container = YappColor.transparent,
                        content =
                            if (enabled) {
                                YappColor.primary
                            } else {
                                YappColor.disabledOutlineContent
                            },
                        border = YappColor.border,
                    )

                YappButtonVariant.OutlinedSecondary ->
                    ButtonColors(
                        container = YappColor.transparent,
                        content =
                            if (enabled) {
                                YappColor.primary
                            } else {
                                YappColor.disabledOutlineContent
                            },
                        border = YappColor.border,
                    )

                YappButtonVariant.OutlinedAssistive ->
                    ButtonColors(
                        container = YappColor.transparent,
                        content =
                            if (enabled) {
                                YappColor.foregroundNeutral
                            } else {
                                YappColor.disabledOutlineContent
                            },
                        border = YappColor.strokeNeutralWeak,
                    )
            }
    }
}
