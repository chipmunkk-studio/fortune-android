package com.android.fortune.presentation.require.terms

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow


class PayFortuneTermsViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(PayFortuneTermsViewState.initial())
    val viewState = _viewState.asStateFlow()

    private val _singleEvent = Channel<PayFortuneTermsSingleEvent>(Channel.BUFFERED)
    val singleEvent = _singleEvent.receiveAsFlow()

    init {

    }

}