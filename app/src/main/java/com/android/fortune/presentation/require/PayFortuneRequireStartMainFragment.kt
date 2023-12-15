package com.android.fortune.presentation.require

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.fortune.R

class PayFortuneRequireStartMainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fortune_require_empty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as? PayFortuneRequireActivity)?.landingToMainActivity()
    }
}

class PayFortuneRequireEmptyFragment() : Fragment()