package com.buddies.common.viewstate

interface ViewStateReducer<VS : ViewState> {
    fun reduce(state: VS): VS
}