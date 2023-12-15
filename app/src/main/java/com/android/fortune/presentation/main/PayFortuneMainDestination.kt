package com.android.fortune.presentation.main

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.fortune.LaunchSingleEvent
import com.android.fortune.PayFortuneExt
import com.android.fortune.PayFortuneExt.deltaThresholdBase
import com.android.fortune.PayFortuneExt.initialZoomLevel
import com.android.fortune.PayFortuneExt.mapMoveAnimationSpeed
import com.android.fortune.domain.PayFortuneMarker
import com.android.fortune.presentation.main.component.PayFortuneMainMap
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity.Companion.CODE_RESULT_OBTAIN_SUCCESS
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity.Companion.KEY_RESULT_OBTAIN_SUCCESS
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainArgs
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
@Composable
fun PayFortuneMainDestination(
    viewModel: PayFortuneMainViewModel,
) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    val rotationListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            viewModel.updateCompass(event.values[0])
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // 정확도 변경 시 처리
        }
    }

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
                Lifecycle.Event.ON_PAUSE -> {
                    // 센서 매니저 등록 해제.
                    sensorManager.unregisterListener(rotationListener)
                }

                Lifecycle.Event.ON_RESUME -> {
                    // 센서 매니저 등록.
                    sensorManager.registerListener(
                        rotationListener,
                        rotationSensor,
                        SensorManager.SENSOR_DELAY_UI
                    )
                }

                Lifecycle.Event.ON_STOP -> {
                    Timber.tag("FortuneTest").d("onStop")
                }

                else -> Unit
            }
        }

        lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            sensorManager.unregisterListener(rotationListener)
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


    if (viewState.isShowRequestObtainDialog) {
        AlertDialog(
            onDismissRequest = {

            },
            text = { Text("마커를 획득하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        val targetMarker = viewState.currentObtainMarker
                        targetMarker?.let {
                            viewModel.startObtainProcess(targetMarker)
                        }
                    }
                ) {
                    Text("확인")
                }
            },
        )
    }

    Box {
        PayFortuneMainMap(
            context = context,
            mapView = mapView,
            headings = viewState.headings,
            markers = viewState.markers,
            currentLocation = viewState.myLocation,
            onMarkerClick = {
                viewModel.onMarkerClick(it)
            }
        )
    }

}