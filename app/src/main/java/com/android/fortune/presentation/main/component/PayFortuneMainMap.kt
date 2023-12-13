package com.android.fortune.presentation.main.component

import PayFortuneDirectionPainter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import com.android.fortune.presentation.main.PayFortuneMapView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.collections.immutable.ImmutableList
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import timber.log.Timber


@Composable
fun PayFortuneMainMap(
    context: Context,
    mapView: PayFortuneMapView,
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
                mapView
            },
        )
        PayFortuneDirectionPainter(headings = headings)
        FortuneMainRippleBackground(
            modifier = Modifier.align(Alignment.Center)
        )

    }
}


@SuppressLint("ClickableViewAccessibility")
fun updateMarkersOnMap(
    currentLocation: GeoPoint,
    markers: ImmutableList<PayFortuneMarker>,
    mapView: PayFortuneMapView,
    context: Context,
    onMarkerClick: (PayFortuneMarker) -> Unit
) {
    mapView.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (mapView.zoomLevelDouble != PayFortuneExt.initialZoomLevel) {
                mapView.controller.animateTo(
                    currentLocation,
                    PayFortuneExt.initialZoomLevel,
                    200L
                )
            }
        }
        false
    }

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
        mapView.overlays.add(PayFortuneGridOverlay())
        mapView.overlays.add(PayFortuneDisableDoubleTabOverlay())
        val newCircleOverlay = PayFortuneCircleOverlay(currentLocation, 200.0)
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
                            icon = BitmapDrawable(context.resources, resource)
                            position = fortuneMarker.location
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }

                        marker.setOnMarkerClickListener { marker, mapView ->
                            if (isMarkerInsideCircle(
                                    marker = marker.position,
                                    circleOverlay = newCircleOverlay,
                                )
                            ) {
                                onMarkerClick(fortuneMarker)
                                Timber.tag("FortuneTest").d("획득 가능한 마커")
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