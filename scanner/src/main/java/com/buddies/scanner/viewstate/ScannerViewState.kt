package com.buddies.scanner.viewstate

import com.buddies.common.model.Tag
import com.buddies.common.viewstate.ViewState
import com.buddies.scanner.R

data class ScannerViewState(
    val showScanner: Boolean = true,
    val showLoading: Boolean = false,
    val showScanAgainButton: Boolean = false,
    val message: Int = R.string.empty,
    val error: String = "",
    val result: Tag? = null
): ViewState