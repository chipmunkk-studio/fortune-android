package com.android.fortune.presentation.require.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.fortune.R
import com.android.fortune.theme.AndroidFortuneTheme

class PayFortuneTermsFragment : Fragment() {
    private val viewModel = PayFortuneTermsViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fortune_terms, container, false).apply {
            findViewById<ComposeView>(R.id.compose_view).apply {
                setContent {
                    AndroidFortuneTheme {
                        PayFortuneTermsDestination(
                            navigator = findNavController(),
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }
}