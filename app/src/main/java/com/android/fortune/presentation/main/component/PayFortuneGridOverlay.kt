package com.android.fortune.presentation.main.component

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class PayFortuneGridOverlay(private val gridSpacing: Float = 50f) : Overlay() {

    private val primaryColor = Color.parseColor("#FF45E2AA") // Define or fetch your primary color

    private val backgroundPaint: Paint = Paint().apply {
        color = Color.parseColor("#FF1A1A1E")
        style = Paint.Style.FILL
    }


    private val gridPaint: Paint = Paint().apply {
        color = primaryColor
        style = Paint.Style.STROKE
        strokeWidth = 0.2f
    }

    private val thickCrossPaint: Paint = Paint().apply {
        color = primaryColor
        style = Paint.Style.STROKE
        strokeWidth = 0.4f
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow) return

        val projection = mapView.projection
        val screenRect = projection.screenRect

        val left = screenRect.left.toFloat()
        val top = screenRect.top.toFloat()
        val right = screenRect.right.toFloat()
        val bottom = screenRect.bottom.toFloat()

        // Draw background
        canvas.drawRect(
            Rect(screenRect.left, screenRect.top, screenRect.right, screenRect.bottom),
            backgroundPaint
        )

        // Draw grid lines
        for (i in left.toInt()..right.toInt() step gridSpacing.toInt()) {
            canvas.drawLine(i.toFloat(), top, i.toFloat(), bottom, gridPaint)
        }
        for (j in top.toInt()..bottom.toInt() step gridSpacing.toInt()) {
            canvas.drawLine(left, j.toFloat(), right, j.toFloat(), gridPaint)
        }

        // Draw thick crosses at intersections
        val crossHalfLength = gridSpacing / 5
        for (i in left.toInt()..right.toInt() step (gridSpacing * 3).toInt()) {
            for (j in top.toInt()..bottom.toInt() step (gridSpacing * 3).toInt()) {
                canvas.drawLine(
                    i - crossHalfLength,
                    j.toFloat(),
                    i + crossHalfLength,
                    j.toFloat(),
                    thickCrossPaint
                )
                canvas.drawLine(
                    i.toFloat(),
                    j - crossHalfLength,
                    i.toFloat(),
                    j + crossHalfLength,
                    thickCrossPaint
                )
            }
        }
    }
}