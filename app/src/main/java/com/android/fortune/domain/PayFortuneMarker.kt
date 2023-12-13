package com.android.fortune.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.osmdroid.util.GeoPoint

@Parcelize
data class PayFortuneMarker(
    val id: String,
    val name: String,
    val location: GeoPoint,
    val imageUrl: String,
    val type: Type
) : Parcelable {
    enum class Type {
        COIN,
        NORMAL,
        NONE,
    }
}