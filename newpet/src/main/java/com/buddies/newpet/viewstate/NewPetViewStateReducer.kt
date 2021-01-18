package com.buddies.newpet.viewstate

import android.net.Uri
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.newpet.R

sealed class NewPetViewStateReducer : ViewStateReducer<NewPetViewState> {

    object ShowScan : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            step = 1,
            forwardButtonEnabled = false,
            forwardButtonExpanded = true,
            forwardButtonText = R.string.no_valid_tags_button_message,
            result = R.string.empty
        )
    }

    object ShowValid : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            forwardButtonEnabled = true,
            forwardButtonExpanded = false,
            result = R.string.validated_result,
        )
    }

    object ShowNotAvailable : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            forwardButtonEnabled = false,
            forwardButtonExpanded = true,
            result = R.string.not_available_result
        )
    }

    data class ShowAnimalPicker(
        val animals: List<Animal>?
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            step = 2,
            forwardButtonEnabled = false,
            forwardButtonExpanded = true,
            forwardButtonText = R.string.no_animal_and_breed_message,
            animalsList = animals ?: emptyList()
        )
    }

    object ShowBreedPicker : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            forwardButtonEnabled = false,
            forwardButtonExpanded = true
        )
    }

    object ShowAnimalAndBreedPicked : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            forwardButtonEnabled = true,
            forwardButtonExpanded = false
        )
    }

    object ShowInfo : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            step = 3,
            forwardButtonEnabled = false,
            forwardButtonExpanded = true,
            forwardButtonText = R.string.no_name_message,
            showCameraOverlay = true
        )
    }

    object ShowInvalidInfo : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            forwardButtonEnabled = false,
            forwardButtonExpanded = true
        )
    }

    object ShowInfoValidated : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            forwardButtonEnabled = true,
            forwardButtonExpanded = false
        )
    }

    data class ShowPetPhoto(
        val photoUri: Uri
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            animalPhoto = photoUri,
            showCameraOverlay = false
        )
    }

    object ShowAddingPet : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            confirmationTitle = R.string.adding_pet_message,
            confirmationLoading = true,
            hideAnimalPhoto = true,
            showBackButton = true
        )
    }

    data class ShowPetConfirmation(
        val name: String
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            confirmationTitle = R.string.pet_added_message,
            confirmationLoading = false,
            hideAnimalPhoto = false,
            showBackButton = false,
            animalName = name
        )
    }
}