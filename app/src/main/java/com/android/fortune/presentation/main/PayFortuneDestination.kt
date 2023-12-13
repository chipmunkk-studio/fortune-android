package com.android.fortune.presentation.main

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.android.fortune.LaunchSingleEvent
import com.android.fortune.PayFortuneExt
import com.android.fortune.PayFortuneExt.deltaThresholdBase
import com.android.fortune.PayFortuneExt.initialZoomLevel
import com.android.fortune.PayFortuneExt.mapMoveAnimationSpeed
import com.android.fortune.domain.PayFortuneMarker
import com.android.fortune.presentation.main.component.PayFortuneMainMap
import com.android.fortune.presentation.main.component.PayFortuneMarkerObtaining
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity.Companion.CODE_RESULT_OBTAIN_SUCCESS
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity.Companion.KEY_RESULT_OBTAIN_SUCCESS
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainArgs
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.overlay.Marker
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
@Composable
fun PayFortuneDestination(
    viewModel: PayFortuneMainViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val mapView = remember { PayFortuneMapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var startAnimation by remember { mutableStateOf<Drawable?>(null) }
    var startPosition by remember { mutableStateOf(Offset(0f, 0f)) }

    // 마커 획득 후 결과를 반환.
    val startObtainActivityForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CODE_RESULT_OBTAIN_SUCCESS) {
            val data = result.data  // Intent 객체
            val resultData = data?.getParcelableExtra<PayFortuneMarker>(KEY_RESULT_OBTAIN_SUCCESS)
            resultData?.let {
                viewModel.obtainSuccess(it)
            }
        }
    }

    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> Timber.tag("FortuneTest").d("onPause")
                Lifecycle.Event.ON_RESUME -> {

                }

                Lifecycle.Event.ON_STOP -> Timber.tag("FortuneTest").d("onStop")
                else -> Unit
            }
        }

        lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }


    LaunchSingleEvent(flow = viewModel.singleEvent) { event ->
        when (event) {
            is PayFortuneSingleEvent.ChangeMyLocation -> {
                val mapController = mapView.controller
                val currentLocation = event.geoPoint
                val deltaThreshold = PayFortuneExt.deltaThreshold
                val deltaLatitude = deltaThreshold / deltaThresholdBase
                val deltaLongitude = deltaThreshold / (deltaThresholdBase * kotlin.math.cos(
                    Math.toRadians(currentLocation.latitude)
                ))
                mapView.setScrollableAreaLimitDouble(
                    BoundingBox(
                        currentLocation.latitude + deltaLatitude,
                        currentLocation.longitude + deltaLongitude,
                        currentLocation.latitude - deltaLatitude,
                        currentLocation.longitude - deltaLongitude,
                    )
                )
                mapController.animateTo(currentLocation, initialZoomLevel, mapMoveAnimationSpeed)
            }

            is PayFortuneSingleEvent.NavigateProcessObtain -> {
                startObtainActivityForResult.launch(
                    PayFortuneMarkerObtainActivity.newIntent(
                        context = context,
                        args = PayFortuneMarkerObtainArgs(
                            marker = event.marker
                        )
                    )
                )
            }

            is PayFortuneSingleEvent.ObtainMarkerAction -> {

                val markerToRemove = mapView.overlays.find { overlay ->
                    overlay is Marker && overlay.id == event.marker.id
                } as? Marker

                markerToRemove?.let {
                    mapView.overlays.remove(markerToRemove)
                    mapView.invalidate()
                }

            }
        }
    }
    Box {
        PayFortuneMainMap(
            context = context,
            mapView = mapView,
            markers = viewState.markers,
            currentLocation = viewState.myLocation,
            onMarkerClick = {
                viewModel.onMarkerClick(it)
            }
        )

        PayFortuneMarkerObtaining(
            visible = viewState.isObtaining,
            imageUrl = viewState.obtainingMarker?.imageUrl,
            name = viewState.obtainingMarker?.name,
        )
    }

}