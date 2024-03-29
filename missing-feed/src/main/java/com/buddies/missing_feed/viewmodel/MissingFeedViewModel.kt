package com.buddies.missing_feed.viewmodel

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToAllMissingPets
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToMissingPet
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToNewMissingPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToProfile
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToSettings
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.missing_feed.usecase.MissingFeedUseCases
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenMoreFoundPets
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenMoreLostPets
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenPetProfile
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenProfile
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenSettings
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.RegisterNewLocation
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.ReportPet
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.RequestFeed
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.Navigate
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.ShowError
import com.buddies.missing_feed.viewstate.MissingFeedViewState
import com.buddies.missing_feed.viewstate.MissingFeedViewStateReducer.HideLoading
import com.buddies.missing_feed.viewstate.MissingFeedViewStateReducer.ShowLoading
import com.buddies.missing_feed.viewstate.MissingFeedViewStateReducer.ShowLocationLoading
import com.buddies.missing_feed.viewstate.MissingFeedViewStateReducer.ShowNearestPetsList
import com.buddies.missing_feed.viewstate.MissingFeedViewStateReducer.ShowPetLists
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class MissingFeedViewModel(
    private val useCases: MissingFeedUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<MissingFeedViewState, MissingFeedViewEffect>(MissingFeedViewState()), CoroutineScope {

    private var currentLocation: Triple<Double, Double, Float>? = null

    fun perform(action: Action) {
        when (action) {
            is OpenSettings -> openSettings()
            is OpenProfile -> openProfile()
            is OpenMoreLostPets -> openAllLostPets()
            is OpenMoreFoundPets -> openAllFoundPets()
            is ReportPet -> openNewMissingPetFlow()
            is RequestFeed -> fetchPetLists()
            is OpenPetProfile -> openPetProfile(action.petId)
            is RegisterNewLocation -> registerNewLocation(action.location)
        }
    }

    private fun fetchPetLists() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val currentUser = useCases.getCurrentUser()
        val currentUserPets = useCases.getCurrentUserPets()
        val mostRecentPets = useCases.getMostRecentPets()
        updateState(ShowPetLists(currentUser?.info?.name, mostRecentPets, currentUserPets))
    }

    private fun openProfile() {
        updateEffect(Navigate(MissingFeedToProfile))
    }

    private fun openPetProfile(petId: String) {
        updateEffect(Navigate(MissingFeedToMissingPet(petId)))
    }

    private fun openAllLostPets() {
        updateEffect(Navigate(MissingFeedToAllMissingPets(LOST.name)))
    }

    private fun openAllFoundPets() {
        updateEffect(Navigate(MissingFeedToAllMissingPets(FOUND.name)))
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
        updateState(ShowLocationLoading)
        val nearestPets = useCases.getNearestPets(currentLocation?.first to currentLocation?.second)
        updateState(ShowNearestPetsList(nearestPets))
    }

    private fun openSettings() {
        updateEffect(Navigate(MissingFeedToSettings))
    }

    private fun showError(error: DefaultError) {
        updateState(HideLoading)
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class OpenPetProfile(val petId: String) : Action()
        data class RegisterNewLocation(val location: Location?) : Action()
        object OpenSettings : Action()
        object OpenProfile : Action()
        object OpenMoreLostPets : Action()
        object OpenMoreFoundPets : Action()
        object ReportPet : Action()
        object RequestFeed : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher
}