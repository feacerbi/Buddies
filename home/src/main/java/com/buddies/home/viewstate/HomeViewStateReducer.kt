package com.buddies.home.viewstate

import com.buddies.common.viewstate.ViewStateReducer

sealed class HomeViewStateReducer : ViewStateReducer<HomeViewState> {

    object IdleHome : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showToolbar = false
            showScanPetButton = true
        }
    }

    object ShowPetScanner : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showToolbar = true
            showScanPetButton = false
        }
    }

    object HidePetScanner : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showToolbar = false
            showScanPetButton = true
        }
    }
}