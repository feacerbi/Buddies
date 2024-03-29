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
import com.buddies.newpet.navigation.NewPetNavDirection.AnimalAndBreedToConfirmation
import com.buddies.newpet.navigation.NewPetNavDirection.FinishFlow
import com.buddies.newpet.navigation.NewPetNavDirection.InfoToAnimalAndBreed
import com.buddies.newpet.navigation.NewPetNavDirection.TagScanToInfo
import com.buddies.newpet.usecase.NewPetUseCases
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.AddNewPet
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.ChooseAnimal
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.ChooseBreed
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.CloseFlow
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.HandleTag
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.InfoInput
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.Next
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.PhotoInput
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.Previous
import com.buddies.newpet.viewstate.NewPetViewEffect
import com.buddies.newpet.viewstate.NewPetViewEffect.Navigate
import com.buddies.newpet.viewstate.NewPetViewEffect.NavigateBack
import com.buddies.newpet.viewstate.NewPetViewEffect.ShowBreeds
import com.buddies.newpet.viewstate.NewPetViewEffect.ShowError
import com.buddies.newpet.viewstate.NewPetViewState
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ResetFlow
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
import com.buddies.newpet.viewstate.NewPetViewStateReducer.ShowValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

class NewPetViewModel(
    private val newPetUseCases: NewPetUseCases
) : StateViewModel<NewPetViewState, NewPetViewEffect>(NewPetViewState()), CoroutineScope {

    private val newPet = NewPet()

    init {
        goToStep1()
    }

    fun perform(action: Action) {
        when (action) {
            is Next -> nextStep()
            is Previous -> previousStep()
            is HandleTag -> handleTag(action.tag)
            is ChooseAnimal -> handleChosenAnimal(action.animal)
            is ChooseBreed -> handleChosenBreed(action.breed)
            is InfoInput -> validateInfo(action.name, action.age)
            is PhotoInput -> savePhoto(action.photo)
            is AddNewPet -> addNewPet()
            is CloseFlow -> closeFlow()
        }
    }

    private fun nextStep() {
        when (viewState.value?.step) {
            1 -> goToStep2()
            2 -> goToStep3()
            3 -> addNewPet()
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
        updateState(ShowScan)
    }

    private fun backToStep1() {
        updateEffect(NavigateBack)
        updateState(ShowScan)
    }

    private fun goToStep2() {
        updateEffect(Navigate(TagScanToInfo))
        updateState(ShowInfo(newPet.name, newPet.photo))
    }

    private fun backToStep2() {
        updateEffect(NavigateBack)
        updateState(ShowInfo(newPet.name, newPet.photo))
    }

    private fun goToStep3() {
        updateEffect(Navigate(InfoToAnimalAndBreed))
        updateState(ShowAnimalsAndBreeds(newPet.animal, newPet.breed))
        requestAnimals()
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

        if (newPet.name?.length ?: 0 >= 2) {
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
        updateEffect(Navigate(AnimalAndBreedToConfirmation))
        updateState(ShowAddingPet(newPet.name))

        newPetUseCases.addNewPet(newPet, OWNER)

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
        object AddNewPet : Action()
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
