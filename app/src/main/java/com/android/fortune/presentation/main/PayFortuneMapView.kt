package com.android.fortune.presentation.main

import android.content.Context
import com.android.fortune.PayFortuneExt
import com.android.fortune.presentation.main.component.PayFortuneDisableDoubleTabOverlay
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

class PayFortuneMapView(context: Context) : MapView(context) {

    init {
        // 더블 탭 막아야 됨.
        overlays.add(PayFortuneDisableDoubleTabOverlay())
        setTileSource(TileSourceFactory.MAPNIK)
        controller.setZoom(PayFortuneExt.initialZoomLevel)
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        zoomController.setZoomInEnabled(false)
        isFlingEnabled = false
        setMultiTouchControls(true)
    }

}