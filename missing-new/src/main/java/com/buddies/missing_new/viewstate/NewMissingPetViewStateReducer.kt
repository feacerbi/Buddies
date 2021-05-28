package com.buddies.missing_new.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.MissingType
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.contact.model.ShareInfoField
import com.buddies.missing_new.R

sealed class NewMissingPetViewStateReducer : ViewStateReducer<NewMissingPetViewState> {

    object ShowTypePicker : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            step = 0,
            showBack = false
        )
    }

    data class ShowAnimalsAndBreeds(
        val animal: Animal?,
        val breed: Breed?
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            step = 2,
            forwardButtonEnabled = animal != null && breed != null,
            forwardButtonExpanded = animal == null || breed == null,
            forwardButtonText = R.string.no_animal_and_breed_message,
            showBack = true
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
        val photoUri: Uri?,
        val type: MissingType,
        val validated: Boolean
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            flowTitle = when (type) {
                LOST -> R.string.report_pet_lost_flow_title
                FOUND -> R.string.report_pet_found_flow_title
            },
            step = 1,
            forwardButtonEnabled = validated,
            forwardButtonExpanded = validated.not(),
            forwardButtonText = when (type) {
                FOUND -> R.string.no_photo_message
                else -> R.string.no_name_and_photo_message
            },
            showCameraOverlay = photoUri == null,
            showName = type == LOST,
            showBack = true
        )
    }

    data class ShowInvalidInfo(
        @StringRes val buttonText: Int
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            forwardButtonEnabled = false,
            forwardButtonExpanded = true,
            forwardButtonText = buttonText
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
            showBack = true
        )
    }

    data class ShowAddingPet(
        val name: String?,
        val type: MissingType
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            step = 4,
            confirmationTitle = when (type) {
                LOST -> R.string.reporting_lost_pet_message
                FOUND -> R.string.reporting_found_pet_message
            },
            confirmationLoading = true,
            hideAnimalPhoto = true,
            animalName = name ?: "",
            showBack = false
        )
    }

    data class ShowPetConfirmation(
        val type: MissingType
    ) : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = state.copy(
            confirmationTitle = when (type) {
                LOST -> R.string.lost_pet_reported_message
                FOUND -> R.string.found_pet_reported_message
            },
            confirmationLoading = false,
            hideAnimalPhoto = false,
            showBack = false
        )
    }

    object ResetFlow : NewMissingPetViewStateReducer() {
        override fun reduce(state: NewMissingPetViewState) = NewMissingPetViewState()
    }
}