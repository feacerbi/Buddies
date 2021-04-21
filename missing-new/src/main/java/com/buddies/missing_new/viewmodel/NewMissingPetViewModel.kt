package com.buddies.missing_new.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.DefaultError
import com.buddies.common.model.InfoType
import com.buddies.common.model.NewMissingPet
import com.buddies.common.util.LocationConverter
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.contact.model.ShareInfoField
import com.buddies.contact.util.toContactInfo
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.AnimalAndBreedToShareInfo
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.FinishFlow
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.InfoToAnimalAndBreed
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.ShareInfoToConfirmation
import com.buddies.missing_new.usecase.NewMissingPetUseCases
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.AddNewMissingPet
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseAnimal
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseBreed
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

class NewMissingPetViewModel(
    private val locationConverter: LocationConverter,
    private val newMissingPetUseCases: NewMissingPetUseCases
) : StateViewModel<NewMissingPetViewState, NewMissingPetViewEffect>(NewMissingPetViewState()), CoroutineScope {

    private val newMissingPet = NewMissingPet()

    init {
        goToStep1()
    }

    fun perform(action: Action) {
        when (action) {
            is Next -> nextStep()
            is Previous -> previousStep()
            is ChooseAnimal -> handleChosenAnimal(action.animal)
            is ChooseBreed -> handleChosenBreed(action.breed)
            is InfoInput -> validateInfo(action.name)
            is PhotoInput -> savePhoto(action.photo)
            is OnFieldsChanged -> verifyCheckedFields(action.list, action.validated)
            is AddNewMissingPet -> addNewMissingPet()
            is CloseFlow -> closeFlow()
        }
    }

    private fun nextStep() {
        when (viewState.value?.step) {
            1 -> goToStep2()
            2 -> goToStep3()
            3 -> addNewMissingPet()
        }
    }

    private fun previousStep() {
        when (viewState.value?.step) {
            1 -> closeFlow()
            2 -> backToStep1()
            3 -> backToStep2()
        }
    }

    private fun goToStep1() {
        updateState(ShowInfo(newMissingPet.name, newMissingPet.photo))
    }

    private fun backToStep1() {
        updateEffect(NavigateBack)
        updateState(ShowInfo(newMissingPet.name, newMissingPet.photo))
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

    private fun validateInfo(name: String) {
        newMissingPet.name = name

        if (newMissingPet.name?.length ?: 0 >= 2) {
            updateState(ShowInfoValidated)
        } else {
            updateState(ShowInvalidInfo)
        }
    }

    private fun savePhoto(uri: Uri) {
        newMissingPet.photo = uri
        updateState(ShowPetPhoto(uri))
    }

    private fun verifyCheckedFields(list: List<ShareInfoField>, validated: Boolean) = safeLaunch(::showError) {
        if (list.any { it.checked } && validated) {
            newMissingPet.contactInfo = list.toContactInfo()
            updateState(ShowInfoValidated)
        } else {
            updateState(ShowInvalidInfo)
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
            ShareInfoField.createLocationField(validCheck = defaultValidationCheck, checked = false)
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
        updateEffect(Navigate(ShareInfoToConfirmation))
        updateState(ShowAddingPet(newMissingPet.name))

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
        object Next : Action()
        object Previous : Action()
        object AddNewMissingPet : Action()
        data class ChooseAnimal(val animal: Animal) : Action()
        data class ChooseBreed(val breed: Breed) : Action()
        data class InfoInput(val name: String) : Action()
        data class PhotoInput(val photo: Uri) : Action()
        data class OnFieldsChanged(val list: List<ShareInfoField>, val validated: Boolean) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    companion object {
        const val CONFIRMATION_DELAY = 2000L
    }
}
