package com.buddies.common.viewstate

interface ViewStateReducer<VS : ViewState> {
    val reduce: VS.() -> Unit
}