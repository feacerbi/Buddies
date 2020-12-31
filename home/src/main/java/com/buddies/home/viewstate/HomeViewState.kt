package com.buddies.home.viewstate

import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewState
import com.buddies.home.R

data class HomeViewState(
    var showScanner: Boolean = false,
    var showLoading: Boolean = false,
    var showScanAgainButton: Boolean = false,
    var scannedName: String = "",
    var showNotifyButton: Boolean = false,
    var showScanPetButton: Boolean = false,
    @StringRes var result: Int = R.string.empty
): ViewState