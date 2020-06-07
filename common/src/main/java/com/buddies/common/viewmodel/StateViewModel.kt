package com.buddies.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buddies.common.livedata.SingleLiveEvent
import com.buddies.common.viewstate.ViewEffect
import com.buddies.common.viewstate.ViewState
import com.buddies.common.viewstate.ViewStateReducer

abstract class StateViewModel<VS : ViewState, VE : ViewEffect>(
    initialState: VS
) : ViewModel() {

    private val viewStateMutable = MutableLiveData(initialState)
    private val viewEffectMutable = SingleLiveEvent<VE>()

    protected val viewState: LiveData<VS> = viewStateMutable
    protected val viewEffect: LiveData<VE> = viewEffectMutable

    protected fun updateState(reducer: ViewStateReducer<VS>) {
        viewStateMutable.value = viewStateMutable.value?.apply {
            reducer.reduce(this)
        }
    }

    protected fun updateEffect(effect: VE) {
        viewEffectMutable.value = effect
    }
}