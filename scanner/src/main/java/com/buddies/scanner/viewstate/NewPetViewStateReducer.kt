package com.buddies.scanner.viewstate

import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.scanner.R

sealed class NewPetViewStateReducer : ViewStateReducer<NewPetViewState> {

    object ShowScan: NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.scan_your_tag_result
            isLoading = false
            showScanButton = false
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    object ShowValidating : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.validating_result
            isLoading = true
            showScanButton = false
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    object ShowValidated : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.validated_result
            isLoading = false
            showScanButton = true
            forwardButtonEnabled = true
            forwardButtonExpanded = false
        }
    }

    object ShowInvalid : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.invalid_result
            isLoading = false
            showScanButton = true
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    object ShowUnrecognized : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.unrecognized_result
            isLoading = false
            showScanButton = true
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }
}