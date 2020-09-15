package com.buddies.home.viewstate

import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewState

data class HomeViewState(
    var showScanner: Boolean = false,
    var showLoading: Boolean = false,
    var showScanAgainButton: Boolean = false,
    var scannedName: String = "",
    var showNotifyButton: Boolean = false,
    var showScanPetButton: Boolean = false,
    @StringRes var result: Int = -1
): ViewState