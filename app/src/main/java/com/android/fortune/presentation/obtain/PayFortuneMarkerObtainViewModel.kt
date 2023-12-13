package com.android.fortune.presentation.obtain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class PayFortuneMarkerObtainViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(PayFortuneMarkerObtainViewState.initial())
    val viewState = _viewState.asStateFlow()

    private val _singleEvent = Channel<PayFortuneMarkerObtainSingleEvent>(Channel.BUFFERED)
    val singleEvent = _singleEvent.receiveAsFlow()

    fun processMarkerObtain(args: PayFortuneMarkerObtainArgs) = viewModelScope.launch {
        delay(1000)
        args.marker?.let {
            _singleEvent.trySend(PayFortuneMarkerObtainSingleEvent.ObtainSuccess(it))
        }
    }

}