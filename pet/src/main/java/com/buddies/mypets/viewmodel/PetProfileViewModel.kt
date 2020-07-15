package com.buddies.mypets.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.DefaultError
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.mypets.usecase.PetUseCases
import com.buddies.mypets.viewmodel.PetProfileViewModel.Action.*
import com.buddies.mypets.viewstate.PetProfileViewEffect
import com.buddies.mypets.viewstate.PetProfileViewEffect.ShowError
import com.buddies.mypets.viewstate.PetProfileViewState
import com.buddies.mypets.viewstate.PetProfileViewStateReducer.ShowInfo
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class PetProfileViewModel(
    private val petId: String,
    private val petUseCases: PetUseCases
) : StateViewModel<PetProfileViewState, PetProfileViewEffect>(PetProfileViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    init {
        refreshPet()
    }

    fun perform(action: Action) {
        when (action) {
            is ChangeName -> updateName(action.name)
            is ChangeTag -> updateTag(action.tag)
            is ChangeAnimal -> updateAnimal(action.animal, action.breed)
            is ChangePhoto -> updatePhoto(action.photo)
        }
    }

    private fun updateName(name: String) = safeLaunch(::showError) {
        petUseCases.updatePetName(petId, name)
        refreshPet()
    }

    private fun updateTag(tag: String) = safeLaunch(::showError) {
        petUseCases.updatePetTag(petId, tag)
        refreshPet()
    }

    private fun updateAnimal(animal: Animal, breed: Breed) = safeLaunch(::showError) {
        petUseCases.updatePetAnimal(petId, animal, breed)
        refreshPet()
    }

    private fun updatePhoto(photo: Uri) = safeLaunch(::showError) {
        petUseCases.updatePetPhoto(petId, photo)
        refreshPet()
    }

    private fun refreshPet() = safeLaunch(::showError) {
        val pet = petUseCases.getPet(petId)
        updateState(ShowInfo(pet))
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class ChangeName(val name: String) : Action()
        data class ChangeTag(val tag: String) : Action()
        data class ChangeAnimal(val animal: Animal, val breed: Breed) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
