package com.buddies.home.viewstate

import com.buddies.common.viewstate.ViewState

data class HomeViewState(
    val showToolbar: Boolean = false,
    val showScanPetButton: Boolean = true
): ViewState