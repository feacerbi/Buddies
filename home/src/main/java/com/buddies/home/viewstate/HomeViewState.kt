package com.buddies.home.viewstate

import com.buddies.common.viewstate.ViewState

data class HomeViewState(
    var showToolbar: Boolean = false,
    var showScanPetButton: Boolean = false
): ViewState