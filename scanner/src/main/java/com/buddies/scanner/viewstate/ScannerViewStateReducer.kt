package com.buddies.scanner.viewstate

import com.buddies.common.model.Tag
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.scanner.R

sealed class ScannerViewStateReducer : ViewStateReducer<ScannerViewState> {


    object StartPetScanner : ScannerViewStateReducer() {
        override val reduce: ScannerViewState.() -> Unit = {
            showScanner = true
            showLoading = false
            showScanAgainButton = false
            message = R.string.scan_your_tag_result
            result = null
        }
    }

    object StopPetScanner : ScannerViewStateReducer() {
        override val reduce: ScannerViewState.() -> Unit = {
            showScanner = false
            result = null
        }
    }

    object ShowValidating : ScannerViewStateReducer() {
        override val reduce: ScannerViewState.() -> Unit = {
            showLoading = true
            showScanAgainButton = false
            message = R.string.validating_result
        }
    }

    data class ShowResult(
        val scannedResult: Tag?
    ) : ScannerViewStateReducer() {
        override val reduce: ScannerViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            message = R.string.validated_result
            result = scannedResult
        }
    }

    object ShowInvalid : ScannerViewStateReducer() {
        override val reduce: ScannerViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            message = R.string.invalid_result
        }
    }

    object ShowUnrecognized : ScannerViewStateReducer() {
        override val reduce: ScannerViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            message = R.string.unrecognized_result
        }
    }

    object ShowError : ScannerViewStateReducer() {
        override val reduce: ScannerViewState.() -> Unit = {
            showLoading = false
            showScanAgainButton = true
            message = R.string.unknown_error_message
        }
    }
}