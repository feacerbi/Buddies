package com.buddies.scanner.viewmodel

import androidx.lifecycle.viewModelScope
import com.buddies.common.model.*
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.navigation.Navigator.NavDirection.TagScanToAnimalAndBreed
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.scanner.usecase.NewPetUseCases
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.*
import com.buddies.scanner.viewstate.NewPetViewEffect
import com.buddies.scanner.viewstate.NewPetViewEffect.*
import com.buddies.scanner.viewstate.NewPetViewState
import com.buddies.scanner.viewstate.NewPetViewStateReducer.*
import kotlinx.coroutines.CoroutineScope
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
            is ScanAgain -> startScan()
            is ChooseAnimal -> handleChosenAnimal(action.animal)
            is ChooseBreed -> handleChosenBreed(action.breed)
            is CloseFlow -> closeFlow()
        }
    }

    private fun nextStep() {
        when (viewState.value?.step) {
            1 -> goToStep2()
            2 -> goToStep3()
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
        val result = newPetUseCases.validateTag(tagValue)

        when (result) {
            true -> {
                newPet.tag = tagValue
                updateState(ShowValidated)
            }
            false -> updateState(ShowInvalid)
        }
    }

    private fun startScan() {
        updateState(ShowScan)
    }

    private fun requestAnimals() = safeLaunch(::showError) {
        val animals = newPetUseCases.getAllAnimals()
        updateState(ShowAnimalPicker(animals))
    }

    private fun handleChosenAnimal(animal: Animal) = safeLaunch(::showError) {
        newPet.animal = animal.id
        val breeds = newPetUseCases.getBreedsFromAnimal(animal.id)
        updateState(ShowBreedPicker)
        updateEffect(ShowBreeds(breeds))
    }

    private fun handleChosenBreed(breed: Breed) {
        newPet.breed = breed.id
        updateState(ShowAnimalAndBreedPicked)
    }

    private fun closeFlow() {

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
        data class ValidateTag(val result: Result<String>) : Action()
        data class ChooseAnimal(val animal: Animal) : Action()
        data class ChooseBreed(val breed: Breed) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
