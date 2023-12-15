package com.android.fortune.presentation.require.location

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.fortune.LaunchSingleEvent
import com.android.fortune.checkPermissionGranted
import com.android.fortune.presentation.main.PayFortuneMainActivity
import com.android.fortune.presentation.require.PayFortuneRequireActivity
import com.android.fortune.presentation.require.location.component.PayFortuneLocationPermissionDialog
import timber.log.Timber


@SuppressLint("ClickableViewAccessibility")
@Composable
fun PayFortuneLocationDestination(
    viewModel: PayFortuneLocationViewModel,
) {
    val context = LocalContext.current
    val activity = context as? PayFortuneRequireActivity
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            activity?.landingToMainActivity()
        } else {
            viewModel.setPermissionState(true)
        }
    }

    // 권한 거부 다이얼로그 표시
    if (viewState.isPermissionGranted) {
        PayFortuneLocationPermissionDialog(activity)
    }

    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> Timber.tag("FortuneTest").d("onPause")
                Lifecycle.Event.ON_RESUME -> {
                    if (context.checkPermissionGranted()) {
                        activity?.landingToMainActivity()
                    } else {
                        permissionLauncher.launch(PayFortuneLocationFragment.requirePermission)
                    }
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
            PayFortuneLocationSingleEvent.Loading -> {

            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "여기는 위치권한 화면입니다")
        Button(onClick = {
            activity?.startActivity(PayFortuneMainActivity.newIntent(context))
            activity?.finish()
        }) {

        }
    }
}
