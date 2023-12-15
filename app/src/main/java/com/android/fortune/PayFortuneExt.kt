package com.android.fortune

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.android.fortune.presentation.main.component.PayFortuneCircleOverlay
import com.android.fortune.presentation.require.location.PayFortuneLocationFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

object PayFortuneExt {
    const val initialZoomLevel = 19.0
    const val deltaThreshold = 600
    const val deltaThresholdBase = 111000.0
    const val mapMoveAnimationSpeed = 500L
    const val minZoomLevel = 18.0
    const val maxZoomLevel = 19.0
}


fun getRandomLocation(currentLocation: GeoPoint): GeoPoint {
    // 10미터 랜덤 이동 계산
    val randomAngle = Math.random() * 2 * Math.PI
    val deltaLatitude = 100 * sin(randomAngle) / 111320
    val deltaLongitude =
        100 * cos(randomAngle) / (111320 * cos(Math.toRadians(currentLocation.latitude)))

    return GeoPoint(
        currentLocation.latitude + deltaLatitude,
        currentLocation.longitude + deltaLongitude
    )
}

fun isMarkerInsideCircle(
    marker: GeoPoint,
    circleOverlay: PayFortuneCircleOverlay
): Boolean {
    // 마커 위치와 원 중심점 사이의 거리를 계산
    val distance = marker.distanceToAsDouble(circleOverlay.center)
    Timber.tag("FortuneTest").d("클릭한 마커까지 거리: $distance")
    // 거리가 원의 반경 이내인지 확인
    return distance <= circleOverlay.radiusInMeters
}

// onStop()에서 컬렉트를 취소하고, onStart()에서 컬렉트를 시작함.
@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner
): Flow<T> {
    return remember(flow, lifecycleOwner) {
        flow.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
}

// 플로우가 변경됬을 경우만 시작함.
@Composable
fun <T> LaunchSingleEvent(
    flow: Flow<T?>, // 1
    function: suspend (value: T) -> Unit // 2
) {
    val effectFlow = rememberFlowWithLifecycle(flow, LocalLifecycleOwner.current) // 3

    LaunchedEffect(effectFlow) { // 4
        effectFlow.mapNotNull { it }.collect(function) // 5
    }
}

// 마커에 '뛰는' 애니메이션을 적용하는 함수
fun applySmoothBounceAnimationToMarker(
    marker: Marker,
    initialPosition: GeoPoint,
    onEnd: () -> Unit
) {
    val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 2000 // 애니메이션 지속 시간
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val delta = Math.sin(progress * Math.PI) * 0.00010 // 애니메이션 변화량
            val newLatitude = initialPosition.latitude + delta
            marker.position = GeoPoint(newLatitude, initialPosition.longitude)
            onEnd()
        }
    }
    animator.start()
}

// 마커 위치를 화면 좌표로 변환하는 함수
fun getScreenPositionFromMarker(marker: Marker, mapView: MapView): Offset {
    val projection = mapView.projection
    val point = projection.toPixels(marker.position, null)
    return Offset(point.x.toFloat(), point.y.toFloat())
}

@Composable
fun Drawable.asImageBitmap(): ImageBitmap {
    val context = LocalContext.current
    return this.toBitmap().asImageBitmap()
}

fun Context.checkPermissionGranted(): Boolean {
    val permissions = PayFortuneLocationFragment.requirePermission
    val allPermissionsGranted = permissions.all { permission ->
        val granted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        granted
    }
    return allPermissionsGranted
}
