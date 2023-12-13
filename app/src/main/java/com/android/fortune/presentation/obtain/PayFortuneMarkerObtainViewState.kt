package com.android.fortune.presentation.obtain

import com.android.fortune.domain.PayFortuneMarker


data class PayFortuneMarkerObtainViewState(
    val marker: PayFortuneMarker?
) {
    companion object {
        fun initial() = PayFortuneMarkerObtainViewState(
            marker = null,
        )
    }
}

sealed interface PayFortuneMarkerObtainSingleEvent {
    data object Loading : PayFortuneMarkerObtainSingleEvent

    data class ObtainSuccess(
        val marker: PayFortuneMarker,
    ) : PayFortuneMarkerObtainSingleEvent
}