package com.android.fortune.presentation.require.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fortune.domain.PayFortuneMarker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PayFortuneLocationViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(PayFortuneLocationViewState.initial())
    val viewState = _viewState.asStateFlow()

    private val _singleEvent = Channel<PayFortuneLocationSingleEvent>(Channel.BUFFERED)
    val singleEvent = _singleEvent.receiveAsFlow()

    fun setPermissionState(flag: Boolean) {
        _viewState.update { prevState ->
            prevState.copy(
                isPermissionGranted = flag
            )
        }
    }

}