package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoMemberUiModel

@Composable
internal fun HomeTeamPhotoMemberRow(
    member: HomeTeamPhotoMemberUiModel,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    val borderColor = if (member.isSelected) YappHomeColor.text else YappHomeColor.divider
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = YappHomeColor.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = member.isSelected,
                        enabled = isEnabled,
                        role = Role.Checkbox,
                        onValueChange = onSelectionChange,
                    ).padding(horizontal = 17.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (member.isSelected) {
                    Image(
                        painter = painterResource(R.drawable.ic_home_team_photo_member_selected),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .size(20.dp)
                                .border(1.dp, YappHomeColor.divider, RoundedCornerShape(4.dp)),
                    )
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    text = member.name,
                    style = YappTypography.scaleT4Medium,
                    color = YappHomeColor.text,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                member.roleLabel?.let {
                    YappText(
                        text = it,
                        style = YappTypography.scaleT3Medium,
                        color = YappHomeColor.subtle,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (member.isCurrentUser) {
                YappText(
                    text = stringResource(R.string.home_team_photo_current_user),
                    style = YappTypography.scaleT3Medium,
                    color = YappHomeColor.brand,
                )
            }
        }
    }
}
