package com.android.fortune.presentation.require.terms


data class PayFortuneTermsViewState(
    val isLoading: Boolean,
) {
    companion object {
        fun initial() = PayFortuneTermsViewState(
            isLoading = false,
        )
    }
}

sealed interface PayFortuneTermsSingleEvent {
    object Loading : PayFortuneTermsSingleEvent
}