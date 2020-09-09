package com.buddies.newpet.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.*
import com.buddies.common.model.OwnershipCategory.OWNER
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.newpet.navigation.NewPetNavDirection.*
import com.buddies.newpet.usecase.NewPetUseCases
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.*
import com.buddies.newpet.viewstate.NewPetViewEffect
import com.buddies.newpet.viewstate.NewPetViewEffect.*
import com.buddies.newpet.viewstate.NewPetViewState
import com.buddies.newpet.viewstate.NewPetViewStateReducer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

class NewPetViewModel(
    private val newPetUseCases: NewPetUseCases
) : StateViewModel<NewPetViewState, NewPetViewEffect>(NewPetViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    private val newPet = NewPet()

    init {
        startScan()
    }

    fun perform(action: Action) {
        when (action) {
            is Next -> nextStep()
            is Previous -> previousStep()
            is ValidateTag -> handleTag(action.result)
            is ScanAgain -> restartScan()
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
            2 -> backToStep1()
            3 -> backToStep2()
        }
    }

    private fun backToStep1() {
        updateEffect(NavigateBack)
        startScan()
    }

    private fun goToStep2() {
        updateEffect(Navigate(TagScanToAnimalAndBreed))
        requestAnimals()
    }

    private fun backToStep2() {
        updateEffect(NavigateBack)
        requestAnimals()
    }

    private fun goToStep3() {
        updateEffect(Navigate(AnimalAndBreedToInfo))
        updateState(ShowInfo)
    }

    private fun handleTag(number: Result<String>) {
        updateEffect(StopCamera)
        updateState(ShowValidating)

        when (number) {
            is Success -> {
                val tagValue = number.data ?: ""
                validateTag(tagValue)
            }
            is Fail -> updateState(ShowUnrecognized)
        }
    }

    private fun validateTag(
        tagValue: String
    ) = safeLaunch(::showError) {
        val tag = newPetUseCases.getTag(tagValue)

        when (tag?.info?.available) {
            true -> {
                newPet.tag = tag.id
                updateState(ShowValidated)
            }
            false -> updateState(ShowInvalid)
        }
    }

    private fun startScan() {
        updateState(ShowScan)
    }

    private fun restartScan() {
        updateEffect(StartCamera)
        startScan()
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
        updateState(ShowAddingPet)
        newPetUseCases.addNewPet(newPet, OWNER)
        updateState(ShowPetConfirmation(newPet.name))
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
        object ScanAgain : Action()
        object Next : Action()
        object Previous : Action()
        object AddNewPet : Action()
        data class ValidateTag(val result: Result<String>) : Action()
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
