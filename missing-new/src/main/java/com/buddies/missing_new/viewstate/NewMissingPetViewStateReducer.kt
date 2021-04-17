package com.buddies.missing_new.viewstate

import android.net.Uri
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.contact.model.ShareInfoField
import com.buddies.missing_new.R

sealed class NewMissingPetViewStateReducer : ViewStateReducer<NewMissingPetViewState> {

    data class ShowAnimalsAndBreeds(
        val animal: Animal?,
        val breed: Breed?
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            step = 2,
            forwardButtonEnabled = animal != null && breed != null,
            forwardButtonExpanded = animal == null || breed == null,
            forwardButtonText = R.string.no_animal_and_breed_message,
        )
    }

    data class ShowAnimalPicker(
        val animals: List<Animal>?
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            animalsList = animals ?: emptyList()
        )
    }

    object ShowBreedPicker : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            forwardButtonEnabled = false,
            forwardButtonExpanded = true
        )
    }

    object ShowAnimalAndBreedPicked : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            forwardButtonEnabled = true,
            forwardButtonExpanded = false
        )
    }

    data class ShowInfo(
        val name: String?,
        val photoUri: Uri?
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            step = 1,
            forwardButtonEnabled = name != null && name.isNotBlank(),
            forwardButtonExpanded = name == null || name.isBlank(),
            forwardButtonText = R.string.no_name_message,
            showCameraOverlay = photoUri == null
        )
    }

    object ShowInvalidInfo : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            forwardButtonEnabled = false,
            forwardButtonExpanded = true
        )
    }

    object ShowInfoValidated : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            forwardButtonEnabled = true,
            forwardButtonExpanded = false
        )
    }

    data class ShowPetPhoto(
        val photoUri: Uri
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            animalPhoto = photoUri,
            showCameraOverlay = false
        )
    }

    data class ShowShareInfo(
        val list: List<ShareInfoField>?
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            step = 3,
            shareInfoFields = list ?: emptyList(),
            forwardButtonEnabled = true,
            forwardButtonExpanded = false,
            forwardButtonText = R.string.no_contact_info_message,
        )
    }

    data class ShowAddingPet(
        val name: String?
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            confirmationTitle = R.string.reporting_pet_message,
            confirmationLoading = true,
            hideAnimalPhoto = true,
            animalName = name ?: ""
        )
    }

    object ShowPetConfirmation : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            confirmationTitle = R.string.pet_reported_message,
            confirmationLoading = false,
            hideAnimalPhoto = false
        )
    }
}