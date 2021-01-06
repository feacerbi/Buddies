package com.buddies.scanner.viewstate

import com.buddies.common.model.Tag
import com.buddies.common.viewstate.ViewState
import com.buddies.scanner.R

data class ScannerViewState(
    var showScanner: Boolean = true,
    var showLoading: Boolean = false,
    var showScanAgainButton: Boolean = false,
    var message: Int = R.string.empty,
    var error: String = "",
    var result: Tag? = null
): ViewState