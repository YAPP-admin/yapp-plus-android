package com.yapp.plus.feature.main.home.model

internal sealed interface HomeInteractionEvent {
    data object ReportCompleted : HomeInteractionEvent
}
