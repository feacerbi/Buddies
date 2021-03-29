package com.buddies.newpet.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.DefaultError
import com.buddies.common.model.NewPet
import com.buddies.common.model.OwnershipCategory.OWNER
import com.buddies.common.model.Tag
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.newpet.navigation.NewPetNavDirection.AnimalAndBreedToInfo
import com.buddies.newpet.navigation.NewPetNavDirection.FinishFlow
import com.buddies.newpet.navigation.NewPetNavDirection.InfoToConfirmation
import com.buddies.newpet.navigation.NewPetNavDirection.StartToAnimalAndBreed
import com.buddies.newpet.navigation.NewPetNavDirection.StartToTagScan
import com.buddies.newpet.navigation.NewPetNavDirection.TagScanToAnimalAndBreed
import com.buddies.newpet.usecase.NewPetUseCases
import com.buddies.newpet.util.FlowType
import com.buddies.newpet.util.FlowType.MISSING
import com.buddies.newpet.util.FlowType.TAG
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.AddNewPet
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.ChooseAnimal
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.ChooseBreed
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.CloseFlow
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.HandleTag
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.InfoInput
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.Next
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.PhotoInput
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.Previous
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.StartFlow
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.StartScanner
import com.buddies.newpet.viewstate.NewPetViewEffect
import com.buddies.newpet.viewstate.NewPetViewEffect.Navigate
import com.buddies.newpet.viewstate.NewPetViewEffect.NavigateBack
import com.buddies.newpet.viewstate.NewPetViewEffect.ShowBreeds
import com.buddies.newpet.viewstate.NewPetViewEffect.ShowError
import com.buddies.newpet.viewstate.NewPetViewState
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowAddingPet
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowAnimalAndBreedPicked
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowAnimalPicker
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowAnimalsAndBreeds
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowBreedPicker
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowInfo
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowInfoValidated
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowInvalidInfo
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowNotAvailable
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowPetConfirmation
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowPetPhoto
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowScan
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowStart
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

class NewPetViewModel(
    private val newPetUseCases: NewPetUseCases
) : StateViewModel<NewPetViewState, NewPetViewEffect>(NewPetViewState()), CoroutineScope {

    private var flowType: FlowType? = null
    private val newPet = NewPet()

    fun perform(action: Action) {
        when (action) {
            is StartFlow -> startFlow(action.flowType, action.tagValue)
            is Next -> nextStep()
            is Previous -> previousStep()
            is StartScanner -> startScan()
            is HandleTag -> handleTag(action.tag)
            is ChooseAnimal -> handleChosenAnimal(action.animal)
            is ChooseBreed -> handleChosenBreed(action.breed)
            is InfoInput -> validateInfo(action.name, action.age)
            is PhotoInput -> savePhoto(action.photo)
            is AddNewPet -> addNewPet()
            is CloseFlow -> closeFlow()
        }
    }

    private fun startFlow(type: FlowType?, tagValue: String) {
        flowType = type
        newPet.tag = tagValue
        updateState(ShowStart(type))
    }

    private fun nextStep() {
        when (viewState.value?.step) {
            0 -> when (flowType) {
                TAG -> goToStep1()
                MISSING -> goToStep2()
                else -> closeFlow()
            }
            1 -> goToStep2()
            2 -> goToStep3()
            3 -> addNewPet()
        }
    }

    private fun previousStep() {
        when (viewState.value?.step) {
            1 -> backToStart()
            2 -> backToStep1()
            3 -> backToStep2()
        }
    }

    private fun backToStart() {
        updateEffect(NavigateBack)
    }

    private fun goToStep1() {
        updateEffect(Navigate(StartToTagScan(newPet.tag)))
    }

    private fun backToStep1() {
        updateEffect(NavigateBack)
        startScan()
    }

    private fun goToStep2() {
        updateState(ShowAnimalsAndBreeds(flowType))
        when (flowType) {
            TAG -> updateEffect(Navigate(TagScanToAnimalAndBreed))
            MISSING -> updateEffect(Navigate(StartToAnimalAndBreed))
            else -> closeFlow()
        }
        requestAnimals()
    }

    private fun backToStep2() {
        updateEffect(NavigateBack)
        requestAnimals()
    }

    private fun goToStep3() {
        updateEffect(Navigate(AnimalAndBreedToInfo))
        updateState(ShowInfo(flowType))
    }

    private fun startScan() {
        updateState(ShowScan)
    }

    private fun handleTag(tag: Tag?) {
        if (tag == null) {
            updateState(ShowScan)
        } else {
            checkAvailability(tag)
        }
    }

    private fun checkAvailability(tag: Tag) {
        when (tag.info.available) {
            true -> {
                newPet.tag = tag.id
                updateState(ShowValid)
            }
            false -> updateState(ShowNotAvailable)
        }
    }

    private fun requestAnimals() = safeLaunch(::showError) {
        val animals = newPetUseCases.getAllAnimals()
        updateState(ShowAnimalPicker(animals))
        updateEffect(ShowBreeds(emptyList()))
    }

    private fun handleChosenAnimal(animal: Animal) = safeLaunch(::showError) {
        newPet.animal = animal
        val breeds = newPetUseCases.getBreedsFromAnimal(animal.id)
        updateState(ShowBreedPicker)
        updateEffect(ShowBreeds(breeds))
    }

    private fun handleChosenBreed(breed: Breed) {
        newPet.breed = breed
        updateState(ShowAnimalAndBreedPicked)
    }

    private fun validateInfo(name: String, age: String) {
        newPet.name = name
        newPet.age = age.toIntOrNull() ?: 0

        if (newPet.name.length >= 2) {
            updateState(ShowInfoValidated)
        } else {
            updateState(ShowInvalidInfo)
        }
    }

    private fun savePhoto(uri: Uri) {
        newPet.photo = uri
        updateState(ShowPetPhoto(uri))
    }

    private fun addNewPet() = safeLaunch(::showError) {
        updateEffect(Navigate(InfoToConfirmation))

        updateState(ShowAddingPet(flowType, newPet.name))
        when (flowType) {
            TAG -> newPetUseCases.addNewPet(newPet, OWNER)
            MISSING -> newPetUseCases.addNewMissingPet(newPet)
        }
        delay(CONFIRMATION_DELAY)

        updateState(ShowPetConfirmation(flowType))
        delay(CONFIRMATION_DELAY)

        closeFlow()
    }

    private fun closeFlow() {
        updateEffect(Navigate(FinishFlow))
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
        startScan()
    }

    sealed class Action {
        object CloseFlow : Action()
        object StartScanner : Action()
        object Next : Action()
        object Previous : Action()
        object AddNewPet : Action()
        data class StartFlow(val flowType: FlowType?, val tagValue: String) : Action()
        data class HandleTag(val tag: Tag?) : Action()
        data class ChooseAnimal(val animal: Animal) : Action()
        data class ChooseBreed(val breed: Breed) : Action()
        data class InfoInput(val name: String, val age: String) : Action()
        data class PhotoInput(val photo: Uri) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    companion object {
        const val CONFIRMATION_DELAY = 2000L
    }
}
