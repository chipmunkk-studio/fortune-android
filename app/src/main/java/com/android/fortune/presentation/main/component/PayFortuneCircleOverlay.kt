package com.android.fortune.presentation.main.component

import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.osmdroid.util.GeoPoint

class PayFortuneCircleOverlay(
    val center: GeoPoint,
    val radiusInMeters: Double
) : Overlay() {
    private val paint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.GREEN // 적절한 색상 설정
        alpha = 30 // 투명도 설정
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (!shadow) {
            val projectedCenter = mapView.projection.toPixels(center, null)
            val radiusInPixels = mapView.projection.metersToPixels(radiusInMeters.toFloat())
            canvas.drawCircle(
                projectedCenter.x.toFloat(),
                projectedCenter.y.toFloat(),
                radiusInPixels,
                paint
            )
        }
    }
}
