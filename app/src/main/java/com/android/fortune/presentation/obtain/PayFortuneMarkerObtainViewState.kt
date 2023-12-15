package com.android.fortune.presentation.obtain

import com.android.fortune.domain.PayFortuneMarker


data class PayFortuneMarkerObtainViewState(
    val targetMarker: PayFortuneMarker?,
    val isObtaining: Boolean,
    val isShowScratchDialog: Boolean,
    val isCloseScratchBox: Boolean,
) {
    companion object {
        fun initial() = PayFortuneMarkerObtainViewState(
            targetMarker = null,
            isObtaining = false,
            isShowScratchDialog = false,
            isCloseScratchBox = false,
        )
    }
}

sealed interface PayFortuneMarkerObtainSingleEvent {

    data class ObtainSuccess(
        val marker: PayFortuneMarker,
    ) : PayFortuneMarkerObtainSingleEvent

    data class ShowScratchMarkerDialog(
        val marker: PayFortuneMarker,
    ) : PayFortuneMarkerObtainSingleEvent
}