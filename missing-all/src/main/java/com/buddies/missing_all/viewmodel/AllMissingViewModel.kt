package com.buddies.missing_all.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.AllMissingPetsToMissingPet
import com.buddies.common.util.Sorting
import com.buddies.common.util.Sorting.MOST_RECENT
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.missing_all.usecase.AllMissingUseCases
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.ChangeSorting
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.OpenPetProfile
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.RequestSorting
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.Search
import com.buddies.missing_all.viewstate.AllMissingViewEffect
import com.buddies.missing_all.viewstate.AllMissingViewEffect.Navigate
import com.buddies.missing_all.viewstate.AllMissingViewEffect.ShowError
import com.buddies.missing_all.viewstate.AllMissingViewEffect.ShowSortingDialog
import com.buddies.missing_all.viewstate.AllMissingViewState
import com.buddies.missing_all.viewstate.AllMissingViewStateReducer.HideSorting
import com.buddies.missing_all.viewstate.AllMissingViewStateReducer.ShowAllPets
import com.buddies.missing_all.viewstate.AllMissingViewStateReducer.ShowSorting
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

    private var currentQuery = ""
    private var currentSorting = MOST_RECENT

    init {
        startPagingMissingPets(sorting = currentSorting)
    }

    fun perform(action: Action) {
        when (action) {
            is OpenPetProfile -> openPetProfile(action.petId)
            is Search -> queryResults(action.query)
            is RequestSorting -> sortingRequested()
            is ChangeSorting -> sortResults(action.sorting)
        }
    }

    private fun openPetProfile(petId: String) {
        updateEffect(Navigate(AllMissingPetsToMissingPet(petId)))
    }

    private fun queryResults(query: String) {
        if (query != currentQuery) {
            supervisorJob.cancelChildren()
            startPagingMissingPets(query, currentSorting)
            currentQuery = query

            if (query.isBlank()) {
                updateState(ShowSorting)
            } else {
                updateState(HideSorting)
            }
        }
    }

    private fun sortResults(sorting: Sorting?) {
        if (sorting != currentSorting) {
            supervisorJob.cancelChildren()
            startPagingMissingPets(currentQuery, sorting ?: currentSorting)
            currentSorting = sorting ?: currentSorting
        }
    }

    private fun startPagingMissingPets(
        query: String? = null,
        sorting: Sorting,
    ) = safeLaunch(::showError) {
        delay(QUERY_DEBOUNCE)
        useCases.getAllPetsWithPaging(query, sorting)
            .cachedIn(this)
            .collectLatest {
                updateState(ShowAllPets(it))
            }
    }

    private fun sortingRequested() {
        updateEffect(ShowSortingDialog(currentSorting))
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        object RequestSorting : Action()
        data class OpenPetProfile(val petId: String) : Action()
        data class Search(val query: String) : Action()
        data class ChangeSorting(val sorting: Sorting?) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + supervisorJob + dispatcher

    companion object {
        private const val QUERY_DEBOUNCE = 500L // Half second
    }
}