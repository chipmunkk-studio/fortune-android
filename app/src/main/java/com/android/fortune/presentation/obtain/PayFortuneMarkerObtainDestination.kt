package com.android.fortune.presentation.obtain

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.fortune.LaunchSingleEvent
import com.android.fortune.domain.PayFortuneMarker
import com.android.fortune.presentation.obtain.component.PayFortuneObtainingView
import com.android.fortune.presentation.obtain.component.PayFortuneScratchCard
import com.android.fortune.presentation.obtain.component.loadNetworkImage
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity.Companion.KEY_RESULT_OBTAIN_SUCCESS
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity.Companion.CODE_RESULT_OBTAIN_SUCCESS
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
@Composable
fun PayFortuneMarkerObtainDestination(
    args: PayFortuneMarkerObtainArgs,
    viewModel: PayFortuneMarkerObtainViewModel,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> Timber.tag("FortuneTest").d("onPause")
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.initProcessMarkerObtain(args)
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
            is PayFortuneMarkerObtainSingleEvent.ObtainSuccess -> {
                val returnIntent = Intent()
                returnIntent.putExtra(KEY_RESULT_OBTAIN_SUCCESS, event.marker)
                activity?.setResult(CODE_RESULT_OBTAIN_SUCCESS, returnIntent)
                activity?.finish()
            }

            is PayFortuneMarkerObtainSingleEvent.ShowScratchMarkerDialog -> {

            }

            else -> {}
        }
    }

    if (viewState.isShowScratchDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text("스크래치 긁기 완료") },
            text = { Text("확인 누르고 획득") },
            confirmButton = {
                Button(
                    onClick = {
                        val targetMarker = viewState.targetMarker
                        targetMarker?.let {
                            viewModel.obtainMarker(it)
                        }
                    }
                ) {
                    Text("확인")
                }
            },
        )
    }


    args.marker?.let { targetMarker ->
        PayFortuneObtainingView(
            visible = viewState.isObtaining,
            imageUrl = targetMarker.imageUrl,
            name = targetMarker.name
        )
        when (targetMarker.type) {
            PayFortuneMarker.Type.RANDOM_BOX -> {
                val sampleBaseImageUrl = "https://picsum.photos/128/128/?random"
                val overlayImage = loadNetworkImage(sampleBaseImageUrl)
                val baseImage = loadNetworkImage(targetMarker.imageUrl)
                val isCloseScratchBox = viewState.isCloseScratchBox
                if (overlayImage != null && baseImage != null) {
                    PayFortuneScratchCard(
                        overlayImage = overlayImage,
                        baseImage = baseImage,
                        isCloseScratchBox = isCloseScratchBox,
                        onScratchEnd = {
                            viewModel.scratchEnd()
                        },
                    )
                }
            }

            else -> Unit
        }
    }


}