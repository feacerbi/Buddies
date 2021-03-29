package com.buddies.newpet.viewstate

import android.net.Uri
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.newpet.R
import com.buddies.newpet.util.FlowType
import com.buddies.newpet.util.FlowType.MISSING
import com.buddies.newpet.util.FlowType.TAG

sealed class NewPetViewStateReducer : ViewStateReducer<NewPetViewState> {

    data class ShowStart(
        val flowType: FlowType?
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            step = 0,
            flowTitle = when (flowType) {
                TAG -> R.string.new_buddy_flow_title
                MISSING -> R.string.report_pet_flow_title
                else -> R.string.empty
            },
            startTitle = when (flowType) {
                TAG -> R.string.add_new_buddy_title
                MISSING -> R.string.report_pet_found_title
                else -> R.string.empty
            },
            startSubtitle = when (flowType) {
                TAG -> R.string.add_new_buddy_subtitle
                MISSING -> R.string.report_pet_found_subtitle
                else -> R.string.empty
            },
            forwardButtonEnabled = true,
            forwardButtonExpanded = false,
            forwardButtonText = R.string.start_flow_message,
        )
    }

    object ShowScan : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            showSteps = true,
            step = 1,
            forwardButtonEnabled = false,
            forwardButtonExpanded = true,
            forwardButtonText = R.string.no_valid_tags_button_message,
            result = R.string.empty,
            showBackButton = true
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

    data class ShowAnimalsAndBreeds(
        val flowType: FlowType?
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            showSteps = when (flowType) {
                TAG -> true
                MISSING -> false
                else -> false
            },
            step = 2,
            forwardButtonEnabled = false,
            forwardButtonExpanded = true,
            forwardButtonText = R.string.no_animal_and_breed_message,
        )
    }

    data class ShowAnimalPicker(
        val animals: List<Animal>?
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
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

    data class ShowInfo(
        val flowType: FlowType?
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            showSteps = when (flowType) {
                TAG -> true
                MISSING -> false
                else -> false
            },
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

    data class ShowAddingPet(
        val flowType: FlowType?,
        val name: String
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            confirmationTitle = when (flowType) {
                TAG -> R.string.adding_pet_message
                MISSING -> R.string.reporting_pet_message
                else -> R.string.empty
            },
            confirmationLoading = true,
            hideAnimalPhoto = true,
            showBackButton = true,
            animalName = name
        )
    }

    data class ShowPetConfirmation(
        val flowType: FlowType?
    ) : NewPetViewStateReducer() {
        override fun reduce(state: NewPetViewState) = state.copy(
            confirmationTitle = when (flowType) {
                TAG -> R.string.pet_added_message
                MISSING -> R.string.pet_reported_message
                else -> R.string.empty
            },
            confirmationLoading = false,
            hideAnimalPhoto = false,
            showBackButton = false
        )
    }
}