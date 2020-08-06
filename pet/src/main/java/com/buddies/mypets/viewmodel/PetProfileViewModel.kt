package com.buddies.mypets.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.buddies.common.model.*
import com.buddies.common.util.ActionTimer
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.mypets.usecase.PetUseCases
import com.buddies.mypets.viewmodel.PetProfileViewModel.Action.*
import com.buddies.mypets.viewstate.PetProfileViewEffect
import com.buddies.mypets.viewstate.PetProfileViewEffect.*
import com.buddies.mypets.viewstate.PetProfileViewState
import com.buddies.mypets.viewstate.PetProfileViewStateReducer.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class PetProfileViewModel(
    private val petId: String,
    private val petUseCases: PetUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<PetProfileViewState, PetProfileViewEffect>(PetProfileViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    private val timer by lazy {
        ActionTimer(this, QUERY_DEBOUNCE_TIME)
    }

    init {
        refreshPet()
    }

    fun perform(action: Action) {
        when (action) {
            is Refresh -> refreshPet()
            is ChangeName -> updateName(action.name)
            is ChangeTag -> updateTag(action.tag)
            is ChangeAnimal -> updateAnimal(action.animal, action.breed)
            is ChangePhoto -> updatePhoto(action.photo)
            is ChangeOwnership -> changeOwnership(action.owner, action.ownership)
            is OpenOwnerProfile -> openOwnerProfile(action.owner)
            is RequestBreeds -> requestBreeds(action.animal)
            is RequestAnimals -> requestAnimals()
            is RequestInviteOwners -> startPagingOwners(action.query)
            is InviteOwner -> inviteOwner(action.owner)
        }
    }

    private fun updateName(name: String) = safeLaunch(::showError) {
        updateState(Loading())
        petUseCases.updatePetName(petId, name)
        refreshPet()
    }

    private fun updateTag(tag: String) = safeLaunch(::showError) {
        updateState(Loading())
        petUseCases.updatePetTag(petId, tag)
        refreshPet()
    }

    private fun updateAnimal(animal: Animal, breed: Breed) = safeLaunch(::showError) {
        updateState(Loading())
        petUseCases.updatePetAnimal(petId, animal.id, breed.id)
        refreshPet()
    }

    private fun updatePhoto(photo: Uri) = safeLaunch(::showError) {
        updateState(Loading())
        petUseCases.updatePetPhoto(petId, photo)
        refreshPet()
    }

    private fun changeOwnership(owner: Owner, ownership: OwnershipCategory) = safeLaunch(::showError) {
        updateState(Loading())
        petUseCases.updateOwnership(petId, owner.user.id, ownership)
        refreshPet()
    }

    private fun refreshPet() = safeLaunch(::showError) {
        updateState(Loading())
        val pet = petUseCases.getPet(petId)
        val animalAndBreed = petUseCases.getAnimalAndBreed(
            pet?.info?.animal ?: "",
            pet?.info?.breed ?: ""
        )
        val owners = petUseCases.getOwnersFromPet(petId)
        val currentOwnership = petUseCases.getCurrentUserPetOwnership(petId)
        updateState(ShowInfo(pet, animalAndBreed, owners, currentOwnership))
    }

    private fun openOwnerProfile(owner: Owner) {
        // TODO
    }

    private fun requestAnimals() = safeLaunch(::showError) {
        updateState(Loading())
        val animals = petUseCases.getAllAnimals()
        updateState(Loading(false))
        updateEffect(ShowAnimalsList(animals))
    }

    private fun requestBreeds(animal: Animal) = safeLaunch(::showError) {
        updateState(Loading())
        val breeds = petUseCases.getBreedsFromAnimal(animal.id)
        updateState(Loading(false))
        updateEffect(ShowBreedsList(breeds, animal))
    }

    private fun inviteOwner(owner: Owner) {
        // TODO Send invitation message/notification
    }

    private fun startPagingOwners(query: String) = timer.restart {
        if (checkNotValidQuery(query)) return@restart

        safeLaunch(::showError) {
            petUseCases.getOwnersToInvite(petId, query).cachedIn(this).collectLatest {
                updateState(ShowOwnersToInvite(it))
            }
        }
    }

    private fun checkNotValidQuery(query: String) = query.length < MIN_QUERY

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class ChangeName(val name: String) : Action()
        data class ChangeTag(val tag: String) : Action()
        data class ChangeAnimal(val animal: Animal, val breed: Breed) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
        data class ChangeOwnership(val owner: Owner, val ownership: OwnershipCategory) : Action()
        data class OpenOwnerProfile(val owner: Owner) : Action()
        data class RequestBreeds(val animal: Animal) : Action()
        data class InviteOwner(val owner: Owner) : Action()
        data class RequestInviteOwners(val query: String) : Action()
        object RequestAnimals : Action()
        object Refresh : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher

    companion object {
        private const val MIN_QUERY = 2
        private const val QUERY_DEBOUNCE_TIME = 500L
    }
}
