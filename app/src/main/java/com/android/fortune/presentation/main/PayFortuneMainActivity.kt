package com.android.fortune.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.fortune.R
import com.android.fortune.databinding.ActivityFortuneMainBinding
import timber.log.Timber

class PayFortuneMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFortuneMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree());
        binding = ActivityFortuneMainBinding.inflate(layoutInflater)
        setMainFragment()
        setContentView(binding.root)
    }

    private fun setMainFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fortune_main_container, PayFortuneMainFragment())
            .commit()
    }

    companion object {
        fun newIntent(
            context: Context,
        ) = Intent(context, PayFortuneMainActivity::class.java).apply {

        }
    }
}