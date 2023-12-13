package com.android.fortune.presentation.main.component

import android.graphics.Canvas
import android.view.MotionEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay


class PayFortuneDisableDoubleTabOverlay : Overlay() {

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
    }

    override fun onDoubleTap(e: MotionEvent?, mapView: MapView?): Boolean {
        return true
    }

}
