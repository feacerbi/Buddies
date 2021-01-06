package com.buddies.scanner.viewstate

import com.buddies.common.viewstate.ViewEffect

sealed class ScannerViewEffect : ViewEffect {
    object StartCamera : ScannerViewEffect()
    object StopCamera : ScannerViewEffect()
}