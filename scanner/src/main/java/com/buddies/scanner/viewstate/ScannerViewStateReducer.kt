package com.buddies.scanner.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.Tag
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.scanner.R

sealed class ScannerViewStateReducer : ViewStateReducer<ScannerViewState> {

    object StartPetScanner : ScannerViewStateReducer() {
        override fun reduce(state: ScannerViewState) = state.copy(
            showScanner = true,
            showLoading = false,
            showScanAgainButton = false,
            message = R.string.scan_your_tag_result,
            result = null
        )
    }

    object StopPetScanner : ScannerViewStateReducer() {
        override fun reduce(state: ScannerViewState) = state.copy(
            showScanner = false
        )
    }

    object ShowValidating : ScannerViewStateReducer() {
        override fun reduce(state: ScannerViewState) = state.copy(
            showLoading = true,
            showScanAgainButton = false,
            message = R.string.validating_result
        )
    }

    data class ShowResult(
        val scannedResult: Tag?
    ) : ScannerViewStateReducer() {
        override fun reduce(state: ScannerViewState) = state.copy(
            showLoading = false,
            showScanAgainButton = true,
            message = R.string.validated_result,
            result = scannedResult
        )
    }

    object ShowInvalid : ScannerViewStateReducer() {
        override fun reduce(state: ScannerViewState) = state.copy(
            showLoading = false,
            showScanAgainButton = true,
            message = R.string.invalid_result
        )
    }

    object ShowUnrecognized : ScannerViewStateReducer() {
        override fun reduce(state: ScannerViewState) = state.copy(
            showLoading = false,
            showScanAgainButton = true,
            message = R.string.unrecognized_result
        )
    }

    data class ShowError(
        @StringRes val error: Int
    ) : ScannerViewStateReducer() {
        override fun reduce(state: ScannerViewState) = state.copy(
            showLoading = false,
            showScanAgainButton = true,
            message = error
        )
    }
}