package com.android.fortune.presentation.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.fortune.R
import com.android.fortune.theme.AndroidFortuneTheme
import java.io.File

class PayFortuneMainFragment : Fragment() {

    private val viewModel = PayFortuneMainViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fortune_main, container, false).apply {
            findViewById<ComposeView>(R.id.compose_view).apply {
                setContent {
                    LaunchedEffect(Unit) {
                        val osmPath =
                            File(Environment.getExternalStorageDirectory(), "osmdroid/tiles")
                        if (!osmPath.exists()) {
                            osmPath.mkdirs()
                        }
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ),
                                1
                            )
                        }
                    }
                    AndroidFortuneTheme {
                        PayFortuneDestination(
                            navController = findNavController(),
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