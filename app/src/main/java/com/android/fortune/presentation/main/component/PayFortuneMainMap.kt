package com.android.fortune.presentation.main.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.android.fortune.PayFortuneExt
import com.android.fortune.applySmoothBounceAnimationToMarker
import com.android.fortune.domain.PayFortuneMarker
import com.android.fortune.isMarkerInsideCircle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.collections.immutable.ImmutableList
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import timber.log.Timber


@Composable
fun PayFortuneMainMap(
    context: Context,
    mapView: MapView,
    headings: Float,
    markers: ImmutableList<PayFortuneMarker>,
    currentLocation: GeoPoint?,
    onMarkerClick: (PayFortuneMarker) -> Unit,
) {
    var previousLocation by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(currentLocation, markers) {
        if (currentLocation != null && currentLocation != previousLocation) {
            previousLocation = currentLocation
            updateMarkersOnMap(
                currentLocation = currentLocation,
                markers = markers,
                mapView = mapView,
                context = context,
                onMarkerClick = onMarkerClick
            )
        }
    }

    Box {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { _ ->
                mapView.apply {
                    minZoomLevel = PayFortuneExt.minZoomLevel
                    maxZoomLevel = PayFortuneExt.maxZoomLevel
                    setTileSource(TileSourceFactory.MAPNIK)
                    setupNightMode(mapView)
                    controller.setZoom(PayFortuneExt.initialZoomLevel)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    zoomController.setZoomInEnabled(false)
                    isFlingEnabled = false
                    setMultiTouchControls(true)
                }
            },
        )
        PayFortuneDirectionPainter()
        FortuneMainRippleBackground(
            modifier = Modifier.align(Alignment.Center)
        )

    }
}


@SuppressLint("ClickableViewAccessibility")
fun updateMarkersOnMap(
    currentLocation: GeoPoint,
    markers: ImmutableList<PayFortuneMarker>,
    mapView: MapView,
    context: Context,
    onMarkerClick: (PayFortuneMarker) -> Unit
) {
    // 이 리스너는 내 위치가 바뀔 때마다 중앙으로 줌인시켜야되기때문에 여기있는거임.
    mapView.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (mapView.zoomLevelDouble != PayFortuneExt.initialZoomLevel) {
                    mapView.controller.animateTo(
                        currentLocation,
                        PayFortuneExt.initialZoomLevel,
                        200L
                    )
                }
                // true로 하면 마커클릭이 안됨.
                // 터치에서 손을땠을 경우 기준.
                false
            }
            // 손가락 하나로 스크롤 방지
            MotionEvent.ACTION_MOVE -> {
                event.pointerCount == 1
            }

            else -> false
        }
    }

    currentLocation.let {
        mapView.overlays.clear()
        mapView.overlays.add(PayFortuneDisableDoubleTabOverlay())
        val newCircleOverlay = PayFortuneCircleOverlay(currentLocation, 100F)
        val newOverlays = ArrayList<Overlay>()
        for (overlay in mapView.overlays) {
            if (overlay !is PayFortuneCircleOverlay) {
                newOverlays.add(overlay)
            }
        }
        newOverlays.add(newCircleOverlay)
        mapView.overlays.addAll(newOverlays)
        markers.forEach { fortuneMarker ->
            Glide.with(context)
                .asBitmap()
                .load(fortuneMarker.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val marker = Marker(mapView).apply {
                            id = fortuneMarker.id
                            title = fortuneMarker.name
                            icon = BitmapDrawable(context.resources, resource)
                            position = fortuneMarker.location
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }

                        marker.setOnMarkerClickListener { marker, mapView ->

                            if (isMarkerInsideCircle(marker.position, newCircleOverlay)) {
                                Timber.tag("FortuneTest").d("획득 가능한 마커")
                                onMarkerClick(fortuneMarker)
                            } else {
                                Timber.tag("FortuneTest").d("획득 불가능한 마커")
                            }
                            true
                        }
                        applySmoothBounceAnimationToMarker(
                            marker = marker,
                            initialPosition = fortuneMarker.location,
                            onEnd = {
                                mapView.invalidate()
                            }
                        )
                        mapView.overlays.add(marker)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })
        }
    }
}

fun setupNightMode(mapView: MapView) {

    // 색상 반전 행렬
    val inverseMatrix = ColorMatrix(
        floatArrayOf(
            -1.0f, 0.0f, 0.0f, 0.0f, 255f,
            0.0f, -1.0f, 0.0f, 0.0f, 255f,
            0.0f, 0.0f, -1.0f, 0.0f, 255f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
        )
    )

    // 목표 색상 설정
    val destinationColor = Color.parseColor("#FF2A2A2A")
    val lr = (255.0f - Color.red(destinationColor)) / 255.0f
    val lg = (255.0f - Color.green(destinationColor)) / 255.0f
    val lb = (255.0f - Color.blue(destinationColor)) / 255.0f

    // 회색조 행렬 생성
    val grayscaleMatrix = ColorMatrix(
        floatArrayOf(
            lr, lg, lb, 0F, 0F,
            lr, lg, lb, 0F, 0F,
            lr, lg, lb, 0F, 0F,
            0F, 0F, 0F, 1F, 0F
        )
    )

    grayscaleMatrix.preConcat(inverseMatrix)

    // 색조 행렬 생성
    val drf = Color.red(destinationColor) / 255f
    val dgf = Color.green(destinationColor) / 255f
    val dbf = Color.blue(destinationColor) / 255f
    val tintMatrix = ColorMatrix(
        floatArrayOf(
            drf, 0F, 0F, 0F, 0F,
            0F, dgf, 0F, 0F, 0F,
            0F, 0F, dbf, 0F, 0F,
            0F, 0F, 0F, 1F, 0F
        )
    )

    tintMatrix.preConcat(grayscaleMatrix)

    // 최종 스케일 행렬 생성 및 적용
    val lDestination = drf * lr + dgf * lg + dbf * lb
    val scale = 1f - lDestination
    val translate = 1 - scale * 0.5f
    val scaleMatrix = ColorMatrix(
        floatArrayOf(
            scale, 0F, 0F, 0F, Color.red(destinationColor) * translate,
            0F, scale, 0F, 0F, Color.green(destinationColor) * translate,
            0F, 0F, scale, 0F, Color.blue(destinationColor) * translate,
            0F, 0F, 0F, 1F, 0F
        )
    )

    scaleMatrix.preConcat(tintMatrix)

    // ColorMatrixColorFilter 생성 및 적용
    val filter = ColorMatrixColorFilter(scaleMatrix)
    mapView.overlayManager.tilesOverlay.setColorFilter(filter)
}