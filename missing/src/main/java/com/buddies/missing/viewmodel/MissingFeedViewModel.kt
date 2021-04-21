package com.buddies.missing.viewmodel

import android.location.Location
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.AllMissingPetsToMissingPet
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToAllMissingPets
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToMissingPet
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToNewMissingPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToProfile
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.missing.usecase.MissingFeedUseCases
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenMorePets
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenPetProfileFromAllPets
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenPetProfileFromFeed
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenProfile
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.RegisterNewLocation
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.ReportPet
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.RequestAllPets
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.RequestFeedPets
import com.buddies.missing.viewstate.MissingFeedViewEffect
import com.buddies.missing.viewstate.MissingFeedViewEffect.Navigate
import com.buddies.missing.viewstate.MissingFeedViewEffect.ShowError
import com.buddies.missing.viewstate.MissingFeedViewState
import com.buddies.missing.viewstate.MissingFeedViewStateReducer.ShowAllPets
import com.buddies.missing.viewstate.MissingFeedViewStateReducer.ShowNearestPetsList
import com.buddies.missing.viewstate.MissingFeedViewStateReducer.ShowPetLists
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

class MissingFeedViewModel(
    private val useCases: MissingFeedUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<MissingFeedViewState, MissingFeedViewEffect>(MissingFeedViewState()), CoroutineScope {

    var currentLocation: Triple<Double, Double, Float>? = null

    fun perform(action: Action) {
        when (action) {
            is OpenProfile -> openProfile()
            is OpenMorePets -> openAllMissingPets()
            is ReportPet -> openNewMissingPetFlow()
            is RequestAllPets -> startPagingMissingPets()
            is RequestFeedPets -> fetchPetLists()
            is OpenPetProfileFromFeed -> openPetProfileFromFeed(action.petId)
            is OpenPetProfileFromAllPets -> openPetProfileFromAllPets(action.petId)
            is RegisterNewLocation -> registerNewLocation(action.location)
        }
    }

    private fun fetchPetLists() = safeLaunch(::showError) {
        val currentUser = useCases.getCurrentUser()
        val currentUserPets = useCases.getCurrentUserPets()
        val mostRecentPets = useCases.getMostRecentPets()
        fetchNearestPets()
        updateState(ShowPetLists(currentUser?.info?.name, mostRecentPets, currentUserPets))
    }

    private fun openProfile() {
        updateEffect(Navigate(MissingFeedToProfile))
    }

    private fun openPetProfileFromFeed(petId: String) {
        updateEffect(Navigate(MissingFeedToMissingPet(petId)))
    }

    private fun openPetProfileFromAllPets(petId: String) {
        updateEffect(Navigate(AllMissingPetsToMissingPet(petId)))
    }

    private fun openAllMissingPets() {
        updateEffect(Navigate(MissingFeedToAllMissingPets))
    }

    private fun openNewMissingPetFlow() {
        updateEffect(Navigate(MissingFeedToNewMissingPetFlow))
    }

    private fun registerNewLocation(location: Location?) = safeLaunch(::showError) {
        location?.let {
            if (it.accuracy > currentLocation?.third ?: 0F) {
                currentLocation = Triple(it.latitude, it.longitude, it.accuracy)
                fetchNearestPets()
            }
        }
    }

    private suspend fun fetchNearestPets() {
        val nearestPets = useCases.getNearestPets(currentLocation?.first to currentLocation?.second)
        updateState(ShowNearestPetsList(nearestPets))
    }

    private fun startPagingMissingPets() = safeLaunch(::showError) {
        useCases.getAllPetsWithPaging()
            .cachedIn(this)
            .collectLatest {
                updateState(ShowAllPets(it))
            }
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class OpenPetProfileFromFeed(val petId: String) : Action()
        data class OpenPetProfileFromAllPets(val petId: String) : Action()
        data class RegisterNewLocation(val location: Location?) : Action()
        object OpenProfile : Action()
        object OpenMorePets : Action()
        object ReportPet : Action()
        object RequestAllPets : Action()
        object RequestFeedPets : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher
}