package com.buddies.home.viewstate

import com.buddies.common.viewstate.ViewStateReducer

sealed class HomeViewStateReducer : ViewStateReducer<HomeViewState> {

    data class Idle(
        val petScannerEnabled: Boolean
    ) : HomeViewStateReducer() {
        override fun reduce(state: HomeViewState) = state.copy(
            showScanPetButton = petScannerEnabled
        )
    }

    object ShowPetScanner : HomeViewStateReducer() {
        override fun reduce(state: HomeViewState) = state.copy(
            showToolbar = true,
            showScanPetButton = false
        )
    }

    object HidePetScanner : HomeViewStateReducer() {
        override fun reduce(state: HomeViewState) = state.copy(
            showToolbar = false,
            showScanPetButton = true
        )
    }
}