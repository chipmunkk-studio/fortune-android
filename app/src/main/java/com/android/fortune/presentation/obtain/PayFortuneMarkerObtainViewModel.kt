package com.android.fortune.presentation.obtain

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


class PayFortuneMarkerObtainViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(PayFortuneMarkerObtainViewState.initial())
    val viewState = _viewState.asStateFlow()

    private val _singleEvent = Channel<PayFortuneMarkerObtainSingleEvent>(Channel.BUFFERED)
    val singleEvent = _singleEvent.receiveAsFlow()



    // todo 이거 Init 블럭으로 옮겨야됨.
    fun initProcessMarkerObtain(args: PayFortuneMarkerObtainArgs) = viewModelScope.launch {
        args.marker?.let {
            _viewState.update { prevState ->
                prevState.copy(
                    targetMarker = args.marker,
                    isObtaining = true,
                )
            }
            when (it.type) {
                PayFortuneMarker.Type.NORMAL,
                PayFortuneMarker.Type.COIN -> {
                    obtainMarker(it)
                }

                PayFortuneMarker.Type.RANDOM_BOX -> {

                }

                else -> Unit
            }
        }
    }


    fun obtainMarker(marker: PayFortuneMarker) = viewModelScope.launch {
        // 랜덤박스 일 경우 스크래치 화면 제거.
        if (marker.type == PayFortuneMarker.Type.RANDOM_BOX) {
            _viewState.update { prevState ->
                prevState.copy(
                    isCloseScratchBox = true,
                    isShowScratchDialog = false,
                )
            }
        }
        // todo 서버에서 마커 획득 api 처리하는 시간.
        delay(1000)
        _viewState.update { prevState ->
            prevState.copy(
                isObtaining = false,
            )
        }
        _singleEvent.trySend(PayFortuneMarkerObtainSingleEvent.ObtainSuccess(marker = marker))
    }


    fun scratchEnd() = viewModelScope.launch {
        _viewState.update { prevState ->
            prevState.copy(
                isShowScratchDialog = true,
            )
        }
    }
}