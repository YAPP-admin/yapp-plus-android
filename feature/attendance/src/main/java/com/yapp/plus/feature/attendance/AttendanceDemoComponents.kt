package com.yapp.plus.feature.attendance

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.button.YappButton
import com.yapp.plus.core.designsystem.component.button.YappButtonSize
import com.yapp.plus.core.designsystem.component.button.YappButtonVariant
import com.yapp.plus.core.designsystem.component.chip.YappChip
import com.yapp.plus.core.designsystem.component.chip.YappChipColor
import com.yapp.plus.core.designsystem.component.chip.YappChipSize
import com.yapp.plus.core.designsystem.component.chip.YappChipStyle
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun HomeSessionCard(
    state: AttendanceUiState<CurrentWeekSessionUiModel>,
    modifier: Modifier = Modifier,
    onAttendanceClick: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = YappMainColor.brand,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            YappText(
                stringResource(R.string.attendance_home_session_label),
                YappTypography.label1NormalMedium,
                YappMainColor.surface,
            )
            when (state) {
                AttendanceUiState.Loading -> HomeSessionLoading()
                AttendanceUiState.Empty ->
                    YappText(
                        stringResource(R.string.attendance_home_session_empty),
                        YappTypography.heading1Bold,
                        YappMainColor.surface,
                    )

                AttendanceUiState.Error -> HomeSessionError(onRetry = onRetry)
                is AttendanceUiState.Content ->
                    HomeSessionContent(
                        session = state.value,
                        onAttendanceClick = onAttendanceClick,
                    )
            }
        }
    }
}

@Composable
private fun HomeSessionLoading() {
    Box(
        modifier = Modifier.fillMaxWidth().height(112.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = YappMainColor.surface,
            modifier = Modifier.size(28.dp),
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun HomeSessionError(onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        YappText(
            stringResource(R.string.attendance_home_session_error),
            YappTypography.body1NormalMedium,
            YappMainColor.surface,
        )
        YappButton(
            text = stringResource(R.string.attendance_retry),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            variant = YappButtonVariant.SolidSecondary,
            size = YappButtonSize.Medium,
        )
    }
}

@Composable
private fun HomeSessionContent(
    session: CurrentWeekSessionUiModel,
    onAttendanceClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            YappText(
                stringResource(
                    R.string.attendance_home_week_range,
                    session.weekStartDateLabel,
                    session.weekEndDateLabel,
                ),
                YappTypography.caption1Bold,
                YappMainColor.surface,
            )
            YappText(session.name, YappTypography.heading1Bold, YappMainColor.surface)
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            AttendanceDetail(
                text = session.dateLabel,
                iconResource = DesignSystemR.drawable.ic_navigation_calendar,
                tint = YappMainColor.surface,
            )
            AttendanceDetail(
                text = session.startTimeLabel,
                iconResource = DesignSystemR.drawable.ic_clock,
                tint = YappMainColor.surface,
            )
            session.location?.let { location ->
                AttendanceDetail(
                    text = location,
                    iconResource = DesignSystemR.drawable.ic_map_pin,
                    tint = YappMainColor.surface,
                )
            }
        }
        YappText(
            text = stringResource(session.attendanceStatus.messageResource),
            style = YappTypography.caption1Bold,
            color = YappMainColor.surface,
        )
        Surface(
            onClick = onAttendanceClick,
            enabled = session.attendanceStatus == AttendanceStatusUiModel.Available,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .alpha(
                        if (session.attendanceStatus == AttendanceStatusUiModel.Available) {
                            1f
                        } else {
                            0.6f
                        },
                    ),
            color = YappMainColor.surface,
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    stringResource(R.string.attendance_home_session_action),
                    YappTypography.label1NormalBold,
                    YappMainColor.brand,
                )
            }
        }
    }
}

private val AttendanceStatusUiModel.messageResource: Int
    get() =
        when (this) {
            AttendanceStatusUiModel.NotEligible -> R.string.attendance_status_not_eligible
            AttendanceStatusUiModel.NotOpen -> R.string.attendance_status_not_open
            AttendanceStatusUiModel.Available -> R.string.attendance_status_available
        }

@Composable
fun AttendanceDetail(
    text: String,
    @DrawableRes iconResource: Int,
    modifier: Modifier = Modifier,
    tint: Color? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = tint ?: YappMainColor.textFaint,
        )
        YappText(
            text,
            YappTypography.caption1Bold,
            tint ?: YappMainColor.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun AttendanceMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(title, YappTypography.body1NormalBold, YappMainColor.textPrimary)
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = YappMainColor.textFaint,
        )
    }
}

@Composable
fun BoardCategorySelector(
    selectedCategory: BoardCategory,
    onCategorySelected: (BoardCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            for (category in BoardCategory.entries) {
                BoardCategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                )
            }
        }
        HorizontalDivider(color = YappMainColor.divider)
    }
}

@Composable
fun BoardCategoryChip(
    category: BoardCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(IntrinsicSize.Min)
                .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
    ) {
        YappText(
            stringResource(category.labelResource),
            YappTypography.body1NormalBold,
            if (isSelected) YappMainColor.textPrimary else YappMainColor.textFaint,
            modifier =
                Modifier
                    .padding(horizontal = 6.dp)
                    .padding(top = 8.dp, bottom = 10.dp),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        if (isSelected) YappMainColor.textPrimary else YappMainColor.surface,
                    ),
        )
    }
}

@Composable
fun AttendanceBoardEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.graphic_none),
            contentDescription = null,
            modifier = Modifier.size(width = 90.dp, height = 74.dp),
        )
        YappText(
            stringResource(R.string.attendance_board_empty),
            YappTypography.body1NormalRegular,
            YappMainColor.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AttendanceScheduleItem(
    session: AttendanceScheduleSessionUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(
                        color =
                            if (session.status == AttendanceScheduleStatusUiModel.Today) {
                                YappMainColor.background
                            } else {
                                YappMainColor.surface
                            },
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 8.dp, vertical = 8.dp)
                    .alpha(
                        if (session.status == AttendanceScheduleStatusUiModel.Past) {
                            0.5f
                        } else {
                            1f
                        },
                    ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            YappText(
                session.dateLabel,
                YappTypography.label1NormalBold,
                YappMainColor.textPrimary,
                modifier = Modifier.width(70.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    YappText(
                        session.name,
                        YappTypography.label1NormalBold,
                        YappMainColor.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (session.status != AttendanceScheduleStatusUiModel.Past) {
                        YappChip(
                            text =
                                stringResource(
                                    if (session.status == AttendanceScheduleStatusUiModel.Today) {
                                        R.string.attendance_schedule_today
                                    } else {
                                        R.string.attendance_schedule_upcoming
                                    },
                                ),
                            color = YappChipColor.CoolNeutral,
                            size = YappChipSize.Small,
                            style = YappChipStyle.Weak,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    YappText(
                        text =
                            stringResource(
                                if (session.type == AttendanceScheduleTypeUiModel.Regular) {
                                    R.string.attendance_schedule_regular
                                } else {
                                    R.string.attendance_schedule_team
                                },
                            ),
                        style = YappTypography.caption1Bold,
                        color = YappMainColor.textSecondary,
                    )
                    session.location?.let { location ->
                        AttendanceDetail(
                            text = location,
                            iconResource = DesignSystemR.drawable.ic_map_pin,
                        )
                    }
                    AttendanceDetail(
                        text = session.timeLabel,
                        iconResource = DesignSystemR.drawable.ic_clock,
                    )
                }
            }
        }
        HorizontalDivider(color = YappMainColor.divider)
    }
}

@Composable
fun AttendanceProfileCard(
    name: String,
    role: String,
    generation: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.graphic_user_profile),
            contentDescription = null,
            modifier = Modifier.size(66.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(name, YappTypography.heading2Bold, YappMainColor.textPrimary)
                YappChip(
                    text = role,
                    color = YappChipColor.Orange,
                    size = YappChipSize.Large,
                    style = YappChipStyle.Fill,
                )
            }
            YappText(generation, YappTypography.label1NormalMedium, YappMainColor.textSecondary)
        }
    }
}

@Composable
fun AttendanceSummaryCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = YappMainColor.surface,
        border = BorderStroke(1.dp, YappMainColor.divider),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(YappMainColor.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    stringResource(R.string.attendance_my_summary_title),
                    YappTypography.label1NormalBold,
                    YappMainColor.textPrimary,
                )
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_help),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified,
                )
                Spacer(Modifier.weight(1f))
                YappText(
                    stringResource(R.string.attendance_my_total_score),
                    YappTypography.label2Medium,
                    YappMainColor.textSecondary,
                )
                YappText(
                    stringResource(R.string.attendance_my_score),
                    YappTypography.body1NormalBold,
                    YappMainColor.brand,
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
            ) {
                AttendanceStatistic(
                    label = stringResource(R.string.attendance_my_present),
                    count = 3,
                    modifier = Modifier.weight(1f),
                )
                AttendanceStatistic(
                    label = stringResource(R.string.attendance_my_late),
                    count = 1,
                    modifier = Modifier.weight(1f),
                )
                AttendanceStatistic(
                    label = stringResource(R.string.attendance_my_absent),
                    count = 0,
                    modifier = Modifier.weight(1f),
                )
                AttendanceStatistic(
                    label = stringResource(R.string.attendance_my_exemption),
                    count = 1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun AttendanceStatistic(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        YappText(label, YappTypography.caption1Bold, YappMainColor.textSecondary)
        YappText(count.toString(), YappTypography.label1NormalBold, YappMainColor.textPrimary)
    }
}
