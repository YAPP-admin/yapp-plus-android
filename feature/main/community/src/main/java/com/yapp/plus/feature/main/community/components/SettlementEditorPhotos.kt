package com.yapp.plus.feature.main.community.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.feature.main.community.R
import com.yapp.plus.feature.main.community.SettlementEditorState
import java.time.Instant

@Composable
internal fun SettlementEditorPhotos(
    state: SettlementEditorState,
    selectedUris: List<String>,
    onPick: () -> Unit,
    onRemoveExisting: (Long) -> Unit,
    onRemoveNew: (String) -> Unit,
) {
    val editable = !state.saving && !state.blocked
    val count = state.photos.size + selectedUris.size
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettlementFormHeading(R.string.community_proofs, optional = true)
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettlementPhotoTrigger(count, editable && count < SettlementDraft.MAX_PHOTOS, onPick)
            val now = Instant.now()
            state.photos.forEachIndexed { index, photo ->
                SettlementPhotoTile(photo.activeUrl(now), index == 0, editable) {
                    onRemoveExisting(photo.id)
                }
            }
            selectedUris.forEachIndexed { index, uri ->
                SettlementPhotoTile(Uri.parse(uri), state.photos.isEmpty() && index == 0, editable) {
                    onRemoveNew(uri)
                }
            }
        }
        SettlementFormGuide(R.string.community_photo_guide)
    }
}

@Composable
internal fun SettlementPhotoTrigger(
    count: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val maxPhotos = SettlementDraft.MAX_PHOTOS
    val description = stringResource(R.string.community_add_photo, count, maxPhotos)
    YappClickableSurface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = YappMainColor.surface,
        border = BorderStroke(1.dp, YappMainColor.border),
        modifier =
            Modifier
                .size(80.dp)
                .semantics { contentDescription = description },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Icon(
                painterResource(R.drawable.settlement_camera),
                contentDescription = null,
                tint = if (enabled) YappMainColor.textSubtle else YappMainColor.textDisabled,
                modifier = Modifier.size(24.dp),
            )
            YappText(
                text = stringResource(R.string.community_photo_count, count, maxPhotos),
                style = YappTypography.scaleT2Medium,
                color = if (enabled) YappMainColor.textPrimary else YappMainColor.textDisabled,
            )
        }
    }
}

@Composable
internal fun SettlementPhotoTile(
    data: Any?,
    isCover: Boolean,
    editable: Boolean,
    onRemove: () -> Unit,
) {
    Box(Modifier.size(80.dp)) {
        Box(
            Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(12.dp))
                .background(YappMainColor.basement),
        ) {
            if (data != null) ProtectedPhoto(data, Modifier.matchParentSize())
            if (isCover) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(YappHomeColor.scrim),
                    contentAlignment = Alignment.Center,
                ) {
                    YappText(
                        text = stringResource(R.string.community_cover_photo),
                        style = YappTypography.scaleT2Medium,
                        color = YappMainColor.surface,
                    )
                }
            }
        }
        if (editable) {
            Image(
                painter = painterResource(R.drawable.settlement_remove),
                contentDescription = stringResource(R.string.community_remove_photo),
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .noRippleClickable(role = Role.Button, onClick = onRemove),
            )
        }
    }
}
