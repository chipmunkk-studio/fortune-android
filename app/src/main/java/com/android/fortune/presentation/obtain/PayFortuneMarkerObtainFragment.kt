package com.android.fortune.presentation.obtain


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.android.fortune.R
import com.android.fortune.presentation.obtain.PayFortuneMarkerObtainActivity.Companion.ARGS_FORTUNE_OPTAIN
import com.android.fortune.theme.AndroidFortuneTheme

class PayFortuneMarkerObtainFragment() : Fragment() {

    private val viewModel = PayFortuneMarkerObtainViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fortune_marker_obtain, container, false).apply {
            findViewById<ComposeView>(R.id.compose_view).apply {
                val intent = (requireActivity() as PayFortuneMarkerObtainActivity).intent
                val args = intent.getParcelableExtra(ARGS_FORTUNE_OPTAIN)
                    ?: PayFortuneMarkerObtainArgs.initial()
                setContent {
                    AndroidFortuneTheme {
                        PayFortuneMarkerObtainDestination(
                            args = args,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}