package com.android.fortune.presentation.main

import com.android.fortune.domain.PayFortuneMarker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.osmdroid.util.GeoPoint

data class PayFortuneMainViewState(
    val markers: ImmutableList<PayFortuneMarker>,
    val myLocation: GeoPoint?,
    val obtainingMarker: PayFortuneMarker?,
    val isObtaining: Boolean,
) {
    companion object {
        fun initial() = PayFortuneMainViewState(
            markers = persistentListOf(),
            myLocation = null,
            obtainingMarker = null,
            isObtaining = false,
        )
    }
}

sealed interface PayFortuneSingleEvent {
    data class ChangeMyLocation(
        val geoPoint: GeoPoint
    ) : PayFortuneSingleEvent

    data class NavigateProcessObtain(
        val marker: PayFortuneMarker
    ) : PayFortuneSingleEvent

    data class ObtainMarkerAction(
        val marker: PayFortuneMarker
    ) : PayFortuneSingleEvent
}