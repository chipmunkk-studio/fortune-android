package com.android.fortune

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.fortune.databinding.ActivityFortuneBinding
import timber.log.Timber

class PayFortuneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFortuneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree());
        binding = ActivityFortuneBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}