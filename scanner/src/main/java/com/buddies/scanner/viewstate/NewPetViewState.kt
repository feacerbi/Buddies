package com.buddies.scanner.viewstate

import com.buddies.common.viewstate.ViewState

class NewPetViewState(
    var result: Int = -1,
    var isLoading: Boolean = false,
    var showScanButton: Boolean = false,
    var forwardButtonExpanded: Boolean = false,
    var forwardButtonEnabled: Boolean = false
) : ViewState