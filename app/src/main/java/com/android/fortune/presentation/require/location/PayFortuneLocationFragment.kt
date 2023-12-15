package com.android.fortune.presentation.require.location

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.android.fortune.R
import com.android.fortune.theme.AndroidFortuneTheme

class PayFortuneLocationFragment : Fragment() {
    private val viewModel = PayFortuneLocationViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fortune_location, container, false).apply {
            findViewById<ComposeView>(R.id.compose_view).apply {
                setContent {
                    AndroidFortuneTheme {
                        PayFortuneLocationDestination(
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }

    companion object {
        val requirePermission = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ).apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // Android 10 (API 레벨 29) 미만인 경우
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}