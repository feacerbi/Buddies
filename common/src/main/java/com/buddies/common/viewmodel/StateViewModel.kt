package com.buddies.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buddies.common.livedata.SingleLiveEvent
import com.buddies.common.viewstate.ViewEffect
import com.buddies.common.viewstate.ViewState
import com.buddies.common.viewstate.ViewStateReducer

abstract class StateViewModel<VS : ViewState, VE : ViewEffect>(
    private val initialState: VS
) : ViewModel() {

    private val viewStateMutable = MutableLiveData(initialState)
    private val viewEffectMutable = SingleLiveEvent<VE>()

    val viewState: LiveData<VS> = viewStateMutable
    val viewEffect: LiveData<VE> = viewEffectMutable

    protected fun updateState(reducer: ViewStateReducer<VS>) {
        viewStateMutable.value = reducer.reduce(viewStateMutable.value ?: initialState)
    }

    protected fun updateEffect(effect: VE) {
        viewEffectMutable.value = effect
    }
}