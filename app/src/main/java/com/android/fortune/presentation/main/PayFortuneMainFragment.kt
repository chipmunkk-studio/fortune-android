package com.android.fortune.presentation.main

import android.content.Context
import android.graphics.Bitmap.Config
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.android.fortune.R
import com.android.fortune.theme.AndroidFortuneTheme
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.util.StorageUtils.getStorage


class PayFortuneMainFragment : Fragment() {

    private val viewModel = PayFortuneMainViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fortune_main, container, false).apply {
            findViewById<ComposeView>(R.id.compose_view).apply {
                val context = requireContext()
                val mapProvider = Configuration.getInstance()
                mapProvider.userAgentValue = BuildConfig.APPLICATION_ID
                mapProvider.load(context, PreferenceManager.getDefaultSharedPreferences(context))
                setContent {
                    AndroidFortuneTheme {
                        PayFortuneMainDestination(
                            viewModel = viewModel
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