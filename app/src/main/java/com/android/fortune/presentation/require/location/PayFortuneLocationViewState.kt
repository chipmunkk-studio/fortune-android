package com.android.fortune.presentation.require.location

import com.android.fortune.domain.PayFortuneMarker


data class PayFortuneLocationViewState(
    val isPermissionGranted: Boolean,
) {
    companion object {
        fun initial() = PayFortuneLocationViewState(
            isPermissionGranted = false,
        )
    }
}

sealed interface PayFortuneLocationSingleEvent {
    object Loading : PayFortuneLocationSingleEvent
}