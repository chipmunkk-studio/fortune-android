package com.android.fortune.presentation.require

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.android.fortune.R
import com.android.fortune.checkPermissionGranted
import com.android.fortune.databinding.ActivityFortuneRequireBinding
import com.android.fortune.presentation.main.PayFortuneMainActivity
import timber.log.Timber

class PayFortuneRequireActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFortuneRequireBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree());
        binding = ActivityFortuneRequireBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fortune_require) as NavHostFragment
        val navController = navHostFragment.navController
        val isPermissionGranted = checkPermissionGranted()
        val isTermsGranted = true
        val startDestinationId = if (!isTermsGranted) {
            R.id.payFortuneTermsFragment
        } else if (!isPermissionGranted) {
            R.id.payFortuneLocationFragment
        } else {
            R.id.payFortuneRequireStartMainFragment
        }
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_fortune_require)
        graph.setStartDestination(startDestinationId)
        navController.graph = graph
    }

    fun landingToMainActivity() {
        startActivity(PayFortuneMainActivity.newIntent(this))
        finish()
    }
}
