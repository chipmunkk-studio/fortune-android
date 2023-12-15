package com.android.fortune.presentation.require.terms

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.navigation.NavController
import com.android.fortune.LaunchSingleEvent
import com.android.fortune.R
import com.android.fortune.checkPermissionGranted
import com.android.fortune.presentation.require.PayFortuneRequireActivity
import timber.log.Timber


@SuppressLint("ClickableViewAccessibility")
@Composable
fun PayFortuneTermsDestination(
    viewModel: PayFortuneTermsViewModel,
    navigator: NavController,
) {
    val context = LocalContext.current
    val activity = context as? PayFortuneRequireActivity
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> Timber.tag("FortuneTest").d("onPause")
                Lifecycle.Event.ON_RESUME -> {
                    Timber.tag("FortuneTest").d("onPause")
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
            PayFortuneTermsSingleEvent.Loading -> {

            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "여기는 약관동의 화면입니다")
        Button(onClick = {
            if (!context.checkPermissionGranted()) {
                navigator.navigate(R.id.payFortuneLocationFragment)
            } else {
                activity?.landingToMainActivity()
            }
        }) {

        }
    }


}