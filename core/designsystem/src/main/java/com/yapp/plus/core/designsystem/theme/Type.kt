package com.yapp.plus.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.R

private val PretendardJp =
    FontFamily(
        Font(R.font.pretendard_jp_regular, FontWeight.Normal),
        Font(R.font.pretendard_jp_medium, FontWeight.Medium),
        Font(R.font.pretendard_jp_semi_bold, FontWeight.SemiBold),
        Font(R.font.pretendard_jp_bold, FontWeight.Bold),
    )

private const val YAPP_FONT_FEATURE_SETTINGS = "\"ss10\" 1"

private fun yappTextStyle(
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit,
) = TextStyle(
    fontFamily = PretendardJp,
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    fontFeatureSettings = YAPP_FONT_FEATURE_SETTINGS,
)

/** Typography tokens from the YAPP Figma type scale. */
object YappTypography {
    val display1Bold = yappTextStyle(FontWeight.Bold, 56.sp, 72.sp, (-1.7864).sp)
    val display2Bold = yappTextStyle(FontWeight.Bold, 40.sp, 52.sp, (-1.128).sp)

    val title1Bold = yappTextStyle(FontWeight.Bold, 36.sp, 48.sp, (-0.972).sp)
    val title2Bold = yappTextStyle(FontWeight.Bold, 28.sp, 38.sp, (-0.6608).sp)
    val title3Bold = yappTextStyle(FontWeight.Bold, 24.sp, 32.sp, (-0.552).sp)

    val heading1Bold = yappTextStyle(FontWeight.SemiBold, 22.sp, 30.sp, (-0.4268).sp)
    val heading2Bold = yappTextStyle(FontWeight.SemiBold, 20.sp, 28.sp, (-0.24).sp)

    val headline1Regular = yappTextStyle(FontWeight.Normal, 18.sp, 26.sp, (-0.0036).sp)
    val headline1Bold = yappTextStyle(FontWeight.SemiBold, 18.sp, 26.sp, (-0.0036).sp)
    val headline2Bold = yappTextStyle(FontWeight.SemiBold, 17.sp, 24.sp, 0.sp)

    val body1NormalRegular = yappTextStyle(FontWeight.Normal, 16.sp, 24.sp, 0.0912.sp)
    val body1NormalMedium = yappTextStyle(FontWeight.Medium, 16.sp, 24.sp, 0.0912.sp)
    val body1NormalBold = yappTextStyle(FontWeight.SemiBold, 16.sp, 24.sp, 0.0912.sp)
    val body1ReadingRegular = yappTextStyle(FontWeight.Normal, 16.sp, 26.sp, 0.0912.sp)
    val body2NormalRegular = yappTextStyle(FontWeight.Normal, 15.sp, 22.sp, 0.144.sp)
    val body2NormalMedium = yappTextStyle(FontWeight.Medium, 15.sp, 22.sp, 0.144.sp)
    val body2NormalBold = yappTextStyle(FontWeight.SemiBold, 15.sp, 22.sp, 0.144.sp)
    val body2ReadingRegular = yappTextStyle(FontWeight.Normal, 15.sp, 24.sp, 0.144.sp)

    val label1NormalMedium = yappTextStyle(FontWeight.Medium, 14.sp, 20.sp, 0.203.sp)
    val label1NormalBold = yappTextStyle(FontWeight.SemiBold, 14.sp, 20.sp, 0.203.sp)
    val label2Regular = yappTextStyle(FontWeight.Normal, 13.sp, 18.sp, 0.2522.sp)
    val label2Medium = yappTextStyle(FontWeight.Medium, 13.sp, 18.sp, 0.2522.sp)
    val label2Bold = yappTextStyle(FontWeight.SemiBold, 13.sp, 18.sp, 0.2522.sp)

    val caption1Bold = yappTextStyle(FontWeight.SemiBold, 12.sp, 16.sp, 0.3024.sp)
    val caption2Bold = yappTextStyle(FontWeight.SemiBold, 11.sp, 14.sp, 0.3421.sp)
}

internal val Typography =
    Typography(
        displayLarge = YappTypography.display1Bold,
        displayMedium = YappTypography.display2Bold,
        displaySmall = YappTypography.title1Bold,
        headlineLarge = YappTypography.title2Bold,
        headlineMedium = YappTypography.title3Bold,
        headlineSmall = YappTypography.heading1Bold,
        titleLarge = YappTypography.heading2Bold,
        titleMedium = YappTypography.headline1Bold,
        titleSmall = YappTypography.headline2Bold,
        bodyLarge = YappTypography.body1NormalRegular,
        bodyMedium = YappTypography.body2NormalRegular,
        bodySmall = YappTypography.label1NormalMedium,
        labelLarge = YappTypography.label1NormalBold,
        labelMedium = YappTypography.label2Medium,
        labelSmall = YappTypography.caption1Bold,
    )
