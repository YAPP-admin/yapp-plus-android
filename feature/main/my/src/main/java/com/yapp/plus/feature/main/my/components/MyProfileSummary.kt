package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.R

sealed interface MyProfileSummaryState {
    data object Loading : MyProfileSummaryState

    data object Empty : MyProfileSummaryState

    data object Error : MyProfileSummaryState

    @JvmInline
    value class Content(
        val displayName: String,
    ) : MyProfileSummaryState
}

@Composable
fun MyProfileSummary(
    state: MyProfileSummaryState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    val profile =
        when (state) {
            is MyProfileSummaryState.Content -> state.displayName
            else -> null
        }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(106.dp)
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.graphic_user_profile),
            contentDescription = null,
            modifier = Modifier.size(66.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            YappText(
                text = profile ?: stringResource(R.string.my_information_title),
                style = YappTypography.headline1Bold,
                color = YappMainColor.textPrimary,
            )
            when (state) {
                MyProfileSummaryState.Loading ->
                    ProfileSubtitle(R.string.my_profile_loading)

                MyProfileSummaryState.Empty -> ProfileSubtitle(R.string.my_profile_unavailable)
                MyProfileSummaryState.Error -> {
                    ProfileSubtitle(R.string.my_profile_error)
                    TextButton(onClick = onRetry) {
                        YappText(
                            text = stringResource(R.string.my_retry),
                            style = YappTypography.label1NormalMedium,
                            color = YappMainColor.brand,
                        )
                    }
                }

                is MyProfileSummaryState.Content -> ProfileSubtitle(R.string.my_profile_test_label)
            }
        }
    }
}

@Composable
private fun ProfileSubtitle(messageResource: Int) {
    YappText(
        text = stringResource(messageResource),
        style = YappTypography.label1NormalMedium,
        color = YappHomeColor.subtle,
    )
}
