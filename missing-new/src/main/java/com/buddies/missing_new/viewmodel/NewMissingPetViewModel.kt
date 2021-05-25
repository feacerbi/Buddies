package com.buddies.missing_new.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.DefaultError
import com.buddies.common.model.InfoType
import com.buddies.common.model.MissingType
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.common.model.NewMissingPet
import com.buddies.common.util.LocationConverter
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.contact.model.ShareInfoField
import com.buddies.contact.util.toContactInfo
import com.buddies.missing_new.R
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.AnimalAndBreedToShareInfo
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.FinishFlow
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.InfoToAnimalAndBreed
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.ShareInfoToConfirmation
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.TypeToInfo
import com.buddies.missing_new.usecase.NewMissingPetUseCases
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.AddNewMissingPet
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseAnimal
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseBreed
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseType
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.CloseFlow
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.InfoInput
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Next
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.OnFieldsChanged
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.PhotoInput
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Previous
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.Navigate
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.NavigateBack
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.ShowBreeds
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.ShowError
import com.buddies.missing_new.viewstate.NewMissingPetViewState
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ResetFlow
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowAddingPet
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowAnimalAndBreedPicked
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowAnimalPicker
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowAnimalsAndBreeds
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowBreedPicker
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowInfo
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowInfoValidated
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowInvalidInfo
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowPetConfirmation
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowPetPhoto
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowShareInfo
import com.buddies.missing_new.viewstate.NewMissingPetViewStateReducer.ShowTypePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

class NewMissingPetViewModel(
    private val locationConverter: LocationConverter,
    private val newMissingPetUseCases: NewMissingPetUseCases
) : StateViewModel<NewMissingPetViewState, NewMissingPetViewEffect>(NewMissingPetViewState()), CoroutineScope {

    private val newMissingPet = NewMissingPet()

    init {
        goToStep0()
    }

    fun perform(action: Action) {
        when (action) {
            is Next -> nextStep(action.validated)
            is Previous -> previousStep()
            is ChooseType -> handleType(action.type)
            is ChooseAnimal -> handleChosenAnimal(action.animal)
            is ChooseBreed -> handleChosenBreed(action.breed)
            is InfoInput -> validateInfo(action.name, action.description)
            is PhotoInput -> savePhoto(action.photo)
            is OnFieldsChanged -> verifyCheckedFields(action.list)
            is AddNewMissingPet -> addNewMissingPet()
            is CloseFlow -> closeFlow()
        }
    }

    private fun nextStep(validated: Boolean) {
        when (viewState.value?.step) {
            0 -> goToStep1()
            1 -> goToStep2()
            2 -> goToStep3()
            3 -> goToStep4(validated)
        }
    }

    private fun previousStep() {
        when (viewState.value?.step) {
            0 -> closeFlow()
            1 -> backToStep0()
            2 -> backToStep1()
            3 -> backToStep2()
            4 -> closeFlow()
        }
    }

    private fun goToStep0() {
        updateState(ShowTypePicker)
    }

    private fun backToStep0() {
        updateEffect(NavigateBack)
        updateState(ShowTypePicker)
    }

    private fun goToStep1() {
        updateEffect(Navigate(TypeToInfo))
        updateState(ShowInfo(null, newMissingPet.type, false))
        validateInfo()
    }

    private fun backToStep1() {
        updateEffect(NavigateBack)
        updateState(ShowInfo(newMissingPet.photo, newMissingPet.type, true))
    }

    private fun goToStep2() {
        updateEffect(Navigate(InfoToAnimalAndBreed))
        updateState(ShowAnimalsAndBreeds(newMissingPet.animal, newMissingPet.breed))
        requestAnimals()
    }

    private fun backToStep2() {
        updateEffect(NavigateBack)
        updateState(ShowAnimalsAndBreeds(newMissingPet.animal, newMissingPet.breed))
    }

    private fun goToStep3() {
        updateEffect(Navigate(AnimalAndBreedToShareInfo))
        createShareInfoFields()
    }

    private fun goToStep4(validated: Boolean) {
        if (validated) {
            updateEffect(Navigate(ShareInfoToConfirmation))
            updateState(ShowAddingPet(newMissingPet.name))
            addNewMissingPet()
        }
    }

    private fun handleType(type: MissingType) {
        newMissingPet.type = type
    }

    private fun requestAnimals() = safeLaunch(::showError) {
        val animals = newMissingPetUseCases.getAllAnimals()
        updateState(ShowAnimalPicker(animals))
        updateEffect(ShowBreeds(emptyList()))
    }

    private fun handleChosenAnimal(animal: Animal) = safeLaunch(::showError) {
        newMissingPet.animal = animal
        val breeds = newMissingPetUseCases.getBreedsFromAnimal(animal.id)
        updateState(ShowBreedPicker)
        updateEffect(ShowBreeds(breeds))
    }

    private fun handleChosenBreed(breed: Breed) {
        newMissingPet.breed = breed
        updateState(ShowAnimalAndBreedPicked)
    }

    private fun validateInfo(
        name: String? = newMissingPet.name,
        description: String = newMissingPet.description
    ) {
        newMissingPet.name = name
        newMissingPet.description = description

        val nameValid = name?.length ?: 0 >= 2
        val photoValid = newMissingPet.photo != null

        when (newMissingPet.type) {
            FOUND -> when {
                photoValid -> updateState(ShowInfoValidated)
                else -> updateState(ShowInvalidInfo(R.string.no_photo_message))
            }
            LOST -> when {
                nameValid.not() && photoValid.not() -> updateState(ShowInvalidInfo(R.string.no_name_and_photo_message))
                nameValid && photoValid.not() -> updateState(ShowInvalidInfo(R.string.no_photo_message))
                nameValid.not() && photoValid -> updateState(ShowInvalidInfo(R.string.no_name_message))
                else -> updateState(ShowInfoValidated)
            }
        }
    }

    private fun savePhoto(uri: Uri) {
        newMissingPet.photo = uri
        updateState(ShowPetPhoto(uri))
        validateInfo()
    }

    private fun verifyCheckedFields(list: List<ShareInfoField>) = safeLaunch(::showError) {
        if (list.any { it.checked }) {
            newMissingPet.contactInfo = list.toContactInfo()
            updateState(ShowInfoValidated)
        } else {
            updateState(ShowInvalidInfo(R.string.no_contact_info_message))
        }
    }

    private fun createShareInfoFields() = safeLaunch(::showError) {
        val currentUser = newMissingPetUseCases.getCurrentUser()
        val userEmail = currentUser?.info?.email ?: ""

        val defaultValidationCheck: (ShareInfoField) -> Boolean = {
            it.checked.not() || it.input.isNotBlank()
        }

        val fields = listOf(
            ShareInfoField.createEmailField(email = userEmail, validCheck = defaultValidationCheck),
            ShareInfoField.cratePhoneField(validCheck = defaultValidationCheck, checked = false),
            ShareInfoField.createLocationField(validCheck = defaultValidationCheck, checked = false, locationConverter = locationConverter)
        )

        updateState(ShowShareInfo(fields))
    }

    private suspend fun checkAndConvertLocation() {
        newMissingPet.contactInfo?.entries?.find { it.key == InfoType.LOCATION }?.let {
            val location = locationConverter.geoPositionFromAddress(it.value)
            newMissingPet.latitude = location.first
            newMissingPet.longitude = location.second
        }
    }

    private fun addNewMissingPet() = safeLaunch(::showError) {
        checkAndConvertLocation()
        newMissingPetUseCases.addNewMissingPet(newMissingPet)

        delay(CONFIRMATION_DELAY)
        updateState(ShowPetConfirmation)

        delay(CONFIRMATION_DELAY)
        closeFlow()
    }

    private fun closeFlow() {
        updateEffect(Navigate(FinishFlow))
        updateState(ResetFlow)
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        object CloseFlow : Action()
        object Previous : Action()
        object AddNewMissingPet : Action()
        data class Next(val validated: Boolean) : Action()
        data class ChooseType(val type: MissingType) : Action()
        data class ChooseAnimal(val animal: Animal) : Action()
        data class ChooseBreed(val breed: Breed) : Action()
        data class InfoInput(val name: String, val description: String) : Action()
        data class PhotoInput(val photo: Uri) : Action()
        data class OnFieldsChanged(val list: List<ShareInfoField>) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    companion object {
        const val CONFIRMATION_DELAY = 2000L
    }
}
