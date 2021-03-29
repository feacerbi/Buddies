package com.buddies.missing.viewmodel

import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToNewPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToProfile
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.missing.usecase.MissingFeedUseCases
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenProfile
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.ReportPet
import com.buddies.missing.viewstate.MissingFeedViewEffect
import com.buddies.missing.viewstate.MissingFeedViewEffect.Navigate
import com.buddies.missing.viewstate.MissingFeedViewEffect.ShowError
import com.buddies.missing.viewstate.MissingFeedViewState
import com.buddies.missing.viewstate.MissingFeedViewStateReducer.ShowPetLists
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class MissingFeedViewModel(
    private val useCases: MissingFeedUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<MissingFeedViewState, MissingFeedViewEffect>(MissingFeedViewState()), CoroutineScope {

    init {
        fetchPetLists()
    }

    fun perform(action: Action) {
        when (action) {
            is OpenProfile -> openProfile()
            is ReportPet -> openNewPetFlow()
        }
    }

    private fun fetchPetLists() = safeLaunch(::showError) {
        val currentUser = useCases.getCurrentUser()
        val currentUserPets = useCases.getCurrentUserPets()
        val mostRecentPets = useCases.getMostRecentPets()
        val nearestPets = useCases.getNearestPets()
        updateState(ShowPetLists(currentUser?.info?.name, mostRecentPets, nearestPets, currentUserPets))
    }

    private fun openProfile() {
        updateEffect(Navigate(MissingFeedToProfile))
    }

    private fun openNewPetFlow() {
        updateEffect(Navigate(MissingFeedToNewPetFlow))
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        object OpenProfile : Action()
        object ReportPet : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher
}