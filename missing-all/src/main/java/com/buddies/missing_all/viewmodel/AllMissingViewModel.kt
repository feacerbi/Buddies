package com.buddies.missing_all.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.AllMissingPetsToMissingPet
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.missing_all.usecase.AllMissingUseCases
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.OpenPetProfile
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.Search
import com.buddies.missing_all.viewstate.AllMissingViewEffect
import com.buddies.missing_all.viewstate.AllMissingViewEffect.Navigate
import com.buddies.missing_all.viewstate.AllMissingViewEffect.ShowError
import com.buddies.missing_all.viewstate.AllMissingViewState
import com.buddies.missing_all.viewstate.AllMissingViewStateReducer.ShowAllPets
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

class AllMissingViewModel(
    private val useCases: AllMissingUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<AllMissingViewState, AllMissingViewEffect>(AllMissingViewState()), CoroutineScope {

    private val supervisorJob = SupervisorJob()

    init {
        startPagingMissingPets()
    }

    fun perform(action: Action) {
        when (action) {
            is OpenPetProfile -> openPetProfile(action.petId)
            is Search -> queryResults(action.query)
        }
    }

    private fun openPetProfile(petId: String) {
        updateEffect(Navigate(AllMissingPetsToMissingPet(petId)))
    }

    private fun queryResults(query: String) {
        supervisorJob.cancelChildren()
        startPagingMissingPets(query)
    }

    private fun startPagingMissingPets(query: String? = null) = safeLaunch(::showError) {
        delay(QUERY_DEBOUNCE)
        useCases.getAllPetsWithPaging(query)
            .cachedIn(this)
            .collectLatest {
                updateState(ShowAllPets(it))
            }
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class OpenPetProfile(val petId: String) : Action()
        data class Search(val query: String) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + supervisorJob + dispatcher

    companion object {
        private const val QUERY_DEBOUNCE = 500L // Half second
    }
}