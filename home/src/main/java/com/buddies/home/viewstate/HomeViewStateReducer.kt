package com.buddies.home.viewstate

import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.home.R

sealed class HomeViewStateReducer : ViewStateReducer<HomeViewState> {

    object IdleHome : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            result = R.string.scan_your_tag_result
            showScanPetButton = true
            showNotifyButton = false
        }
    }

    object StartPetScanner : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showScanner = true
            showLoading = false
            showScanAgainButton = false
            showNotifyButton = false
            result = R.string.scan_your_tag_result
        }
    }

    object StopPetScanner : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showScanner = false
            showScanPetButton = true
            showNotifyButton = false
        }
    }

    object ShowValidating : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showLoading = true
            showScanAgainButton = false
            result = R.string.validating_result
        }
    }

    data class ShowLostPet(
        val name: String?
    ) : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            scannedName = name ?: ""
            showNotifyButton = true
            showScanPetButton = false
            result = R.string.lost_pet_found
        }
    }

    data class ShowPet(
        val name: String?
    ) : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            scannedName = name ?: ""
            result = R.string.pet_found
        }
    }

    object ShowInvalid : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            result = R.string.empty_tag_message
        }
    }

    object ShowUnrecognized : HomeViewStateReducer() {
        override val reduce: HomeViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            result = R.string.unrecognized_result
        }
    }
}