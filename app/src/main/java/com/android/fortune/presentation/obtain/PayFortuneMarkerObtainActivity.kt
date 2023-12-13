package com.android.fortune.presentation.obtain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.android.fortune.databinding.ActivityFortuneObtainBinding
import com.android.fortune.domain.PayFortuneMarker
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Parcelize
data class PayFortuneMarkerObtainArgs(
    val marker: PayFortuneMarker?
) : Parcelable {
    companion object {
        fun initial() = PayFortuneMarkerObtainArgs(
            marker = null,
        )
    }
}
class PayFortuneMarkerObtainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFortuneObtainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFortuneObtainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        const val ARGS_FORTUNE_OPTAIN = "ARGS_FORTUNE_OPTAIN"
        const val KEY_RESULT_OBTAIN_SUCCESS = "KEY_RESULT_OBTAIN_SUCCESS"
        const val CODE_RESULT_OBTAIN_SUCCESS = 1004
        fun newIntent(
            context: Context,
            args: PayFortuneMarkerObtainArgs
        ) = Intent(context, PayFortuneMarkerObtainActivity::class.java).apply {
            putExtra(ARGS_FORTUNE_OPTAIN, args)
        }
    }
}