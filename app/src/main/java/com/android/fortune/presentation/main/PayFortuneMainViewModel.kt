package com.android.fortune.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fortune.domain.PayFortuneMarker
import com.android.fortune.getRandomLocation
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


class PayFortuneMainViewModel : ViewModel() {


    private val _viewState = MutableStateFlow(PayFortuneMainViewState.initial())
    val viewState = _viewState.asStateFlow()

    private val _singleEvent = Channel<PayFortuneSingleEvent>(Channel.BUFFERED)
    val singleEvent = _singleEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            // 현재 위치를 불러옴.
            val centerGeoPoint = GeoPoint(37.39481622873532, 127.1112430592695)
            // 마커 리스트.
            val sampleList = (0..9).map {
                val id = "$it"
                val angle = 2 * Math.PI / 10 * it // 각 마커의 각도
                val offsetX = 100 * kotlin.math.cos(angle) / 110540 // 경도 오프셋 (대략적인 계산)
                val offsetY = 100 * kotlin.math.sin(angle) / 111320 // 위도 오프셋 (대략적인 계산)

                val markerPoint =
                    GeoPoint(centerGeoPoint.latitude + offsetY, centerGeoPoint.longitude + offsetX)

                PayFortuneMarker(
                    id = id,
                    name = when (it) {
                        0 -> PayFortuneMarker.Type.COIN.name
                        1 -> PayFortuneMarker.Type.NORMAL.name
                        2 -> PayFortuneMarker.Type.RANDOM_BOX.name
                        3 -> PayFortuneMarker.Type.COIN.name
                        4 -> PayFortuneMarker.Type.NORMAL.name
                        5 -> PayFortuneMarker.Type.RANDOM_BOX.name
                        else -> PayFortuneMarker.Type.RANDOM_BOX.name
                    },
                    location = markerPoint,
                    imageUrl = "https://hcpyrleocxaejkqqibik.supabase.co/storage/v1/object/public/ingredients/green_clover.webp?t=2023-12-15T03%3A36%3A26.315Z",
                    type = when (it) {
                        0 -> PayFortuneMarker.Type.COIN
                        1 -> PayFortuneMarker.Type.NORMAL
                        2 -> PayFortuneMarker.Type.RANDOM_BOX
                        3 -> PayFortuneMarker.Type.COIN
                        4 -> PayFortuneMarker.Type.NORMAL
                        5 -> PayFortuneMarker.Type.RANDOM_BOX
                        else -> PayFortuneMarker.Type.RANDOM_BOX
                    },
                )
            }.toImmutableList()

            // 내 위치를 보냄.
            _singleEvent.trySend(PayFortuneSingleEvent.ChangeMyLocation(centerGeoPoint))
            _viewState.update { prevState ->
                prevState.copy(
                    myLocation = centerGeoPoint,
                    markers = sampleList
                )
            }
            startLocationUpdates(centerGeoPoint)
        }
    }

    private fun startLocationUpdates(centerGeoPoint: GeoPoint) {
        viewModelScope.launch {
            while (true) {
                // 현재 내 위치를 가져옴.(서버에서 가져온걸로)
                val newLocation = getRandomLocation(centerGeoPoint)
                // 새로운 마커 들을 가져옴. (서버에서 가져온걸로)
                val nextList = (0..9).map {
                    val id = "${System.currentTimeMillis()}-$it"
                    val angle = 2 * Math.PI / 10 * it // 각 마커의 각도
                    val offsetX = 200 * kotlin.math.cos(angle) / 110540 // 경도 오프셋 (대략적인 계산)
                    val offsetY = 200 * kotlin.math.sin(angle) / 111320 // 위도 오프셋 (대략적인 계산)

                    val markerPoint =
                        GeoPoint(newLocation.latitude + offsetY, newLocation.longitude + offsetX)

                    PayFortuneMarker(
                        id = id,
                        name = when (it) {
                            0 -> PayFortuneMarker.Type.COIN.name
                            1 -> PayFortuneMarker.Type.NORMAL.name
                            2 -> PayFortuneMarker.Type.RANDOM_BOX.name
                            3 -> PayFortuneMarker.Type.COIN.name
                            4 -> PayFortuneMarker.Type.NORMAL.name
                            5 -> PayFortuneMarker.Type.RANDOM_BOX.name
                            else -> PayFortuneMarker.Type.RANDOM_BOX.name
                        },
                        location = markerPoint,
                        imageUrl = "https://picsum.photos/128/128/?random",
                        type = when (it) {
                            0 -> PayFortuneMarker.Type.COIN
                            1 -> PayFortuneMarker.Type.NORMAL
                            2 -> PayFortuneMarker.Type.RANDOM_BOX
                            3 -> PayFortuneMarker.Type.COIN
                            4 -> PayFortuneMarker.Type.NORMAL
                            5 -> PayFortuneMarker.Type.RANDOM_BOX
                            else -> PayFortuneMarker.Type.RANDOM_BOX
                        },
                    )
                }.toImmutableList()

                _viewState.update { currentState ->
                    currentState.copy(
                        myLocation = newLocation,
                        markers = nextList
                    )
                }

                _singleEvent.send(PayFortuneSingleEvent.ChangeMyLocation(newLocation))
                delay(10000)
            }
        }
    }

    fun onMarkerClick(marker: PayFortuneMarker) {
        _viewState.update { prevState ->
            prevState.copy(
                isShowRequestObtainDialog = true,
                currentObtainMarker = marker,
            )
        }
    }

    fun startObtainProcess(marker: PayFortuneMarker) {
        _viewState.update { prevState ->
            prevState.copy(
                isShowRequestObtainDialog = false,
            )
        }
        _singleEvent.trySend(PayFortuneSingleEvent.NavigateProcessObtain(marker))
    }

    fun obtainSuccess(marker: PayFortuneMarker) = viewModelScope.launch {
        _singleEvent.trySend(PayFortuneSingleEvent.ObtainMarkerAction(marker))
    }

    fun updateCompass(azimuth: Float) {
        _viewState.update { prevState ->
            prevState.copy(
                headings = azimuth
            )
        }
    }

}