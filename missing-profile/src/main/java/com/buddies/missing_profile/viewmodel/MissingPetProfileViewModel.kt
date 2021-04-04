package com.buddies.missing_profile.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.DefaultError
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.missing_profile.usecase.MissingPetUseCases
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeAnimal
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeName
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangePhoto
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.Refresh
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestAnimals
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestBreeds
import com.buddies.missing_profile.viewstate.MissingPetViewEffect
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowAnimalsList
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowBreedsList
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowError
import com.buddies.missing_profile.viewstate.MissingPetViewState
import com.buddies.missing_profile.viewstate.MissingPetViewStateReducer.HideLoading
import com.buddies.missing_profile.viewstate.MissingPetViewStateReducer.ShowInfo
import com.buddies.missing_profile.viewstate.MissingPetViewStateReducer.ShowLoading
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class MissingPetProfileViewModel(
    private val petId: String,
    private val missingPetUseCases: MissingPetUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<MissingPetViewState, MissingPetViewEffect>(MissingPetViewState()), CoroutineScope {

    init {
        refreshPet()
    }

    fun perform(action: Action) {
        when (action) {
            is Refresh -> refreshPet()
            is ChangeName -> updateName(action.name)
            is ChangeAnimal -> updateAnimal(action.animal, action.breed)
            is ChangePhoto -> updatePhoto(action.photo)
            is RequestBreeds -> requestBreeds(action.animal)
            is RequestAnimals -> requestAnimals()
        }
    }

    private fun updateName(name: String) = safeLaunch(::showError) {
        updateState(ShowLoading)
        missingPetUseCases.updatePetName(petId, name)
        refreshPet()
    }

    private fun updateAnimal(animal: Animal, breed: Breed) = safeLaunch(::showError) {
        updateState(ShowLoading)
        missingPetUseCases.updatePetAnimal(petId, animal.id, breed.id)
        refreshPet()
    }

    private fun updatePhoto(photo: Uri) = safeLaunch(::showError) {
        updateState(ShowLoading)
        missingPetUseCases.updatePetPhoto(petId, photo)
        refreshPet()
    }

    private fun refreshPet() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val pet = missingPetUseCases.getPet(petId)
        val animalAndBreed = missingPetUseCases.getAnimalAndBreed(
            pet?.info?.animal ?: "",
            pet?.info?.breed ?: ""
        )
        val user = missingPetUseCases.getCurrentUser()
        updateState(ShowInfo(pet, animalAndBreed, user))
    }

    private fun requestAnimals() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val animals = missingPetUseCases.getAllAnimals()
        updateState(HideLoading)
        updateEffect(ShowAnimalsList(animals))
    }

    private fun requestBreeds(animal: Animal) = safeLaunch(::showError) {
        updateState(ShowLoading)
        val breeds = missingPetUseCases.getBreedsFromAnimal(animal.id)
        updateState(HideLoading)
        updateEffect(ShowBreedsList(breeds, animal))
    }

    private fun showError(error: DefaultError) {
        updateState(HideLoading)
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class ChangeName(val name: String) : Action()
        data class ChangeAnimal(val animal: Animal, val breed: Breed) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
        data class RequestBreeds(val animal: Animal) : Action()
        object RequestAnimals : Action()
        object Refresh : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher
}
