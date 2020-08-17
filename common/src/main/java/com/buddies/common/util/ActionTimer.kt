package com.buddies.common.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActionTimer(
    private val scope: CoroutineScope,
    private val timeout: Long
) {

    private var job: Job? = null

    var available = true

    fun start(
        action: suspend () -> Unit
    ) {
        job = scope.launch {
            delay(timeout)
            action.invoke()
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun restart(
        action: suspend () -> Unit
    ) {
        stop()
        start(action)
    }

    fun debounce() {
        job?.cancel()
        job = scope.launch {
            available = false
            delay(timeout)
            available = true
        }
    }
}