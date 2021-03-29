package com.buddies.pet.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.buddies.common.model.*
import com.buddies.common.util.ActionTimer
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.pet.R
import com.buddies.pet.usecase.PetUseCases
import com.buddies.pet.viewmodel.PetProfileViewModel.Action.*
import com.buddies.pet.viewstate.PetProfileViewEffect
import com.buddies.pet.viewstate.PetProfileViewEffect.*
import com.buddies.pet.viewstate.PetProfileViewState
import com.buddies.pet.viewstate.PetProfileViewStateReducer.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

class PetProfileViewModel(
    private val petId: String,
    private val petUseCases: PetUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<PetProfileViewState, PetProfileViewEffect>(PetProfileViewState()), CoroutineScope {

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
            is ReportLost -> reportLost(action.name, action.lost, action.switchChecked)
            is HandleTag -> handleTag(action.tag)
            is OpenScanner -> startScanner()
            is ConfirmLost -> confirmLost(action.name)
            is CancelLost -> cancelLost()
            is ToggleFavorite -> toggleFavorite()
        }
    }

    private fun updateName(name: String) = safeLaunch(::showError) {
        updateState(ShowLoading)
        petUseCases.updatePetName(petId, name)
        refreshPet()
    }

    private fun updateTag(tag: String) = safeLaunch(::showError) {
        updateState(ShowLoading)
        petUseCases.updatePetTag(petId, tag)
        refreshPet()
    }

    private fun updateAnimal(animal: Animal, breed: Breed) = safeLaunch(::showError) {
        updateState(ShowLoading)
        petUseCases.updatePetAnimal(petId, animal.id, breed.id)
        refreshPet()
    }

    private fun updatePhoto(photo: Uri) = safeLaunch(::showError) {
        updateState(ShowLoading)
        petUseCases.updatePetPhoto(petId, photo)
        refreshPet()
    }

    private fun changeOwnership(owner: Owner, ownership: OwnershipCategory) = safeLaunch(::showError) {
        updateState(ShowLoading)
        petUseCases.updateOwnership(petId, owner.user.id, ownership)
        refreshPet()
    }

    private fun reportLost(
        name: String,
        lost: Boolean,
        switchChecked: Boolean
    ) = safeLaunch(::showError) {
        if (switchChecked != lost) {
            if (switchChecked) {
                updateState(ShowLostStatus)
                updateEffect(ShowConfirmDialog(R.string.report_lost_message, name))
            } else {
                updateState(ShowSafeStatus)
                petUseCases.updateLostStatus(petId, false)
                updateEffect(ShowBottomMessage(R.string.pet_report_safe_confirmation, arrayOf(name)))
            }
        }
    }

    private fun confirmLost(name: String) = safeLaunch(::showError) {
        petUseCases.updateLostStatus(petId, true)
        updateEffect(ShowBottomMessage(R.string.pet_report_lost_confirmation, arrayOf(name)))
    }

    private fun cancelLost() {
        updateState(ShowSafeStatus)
    }

    private fun refreshPet() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val pet = petUseCases.getPet(petId)
        val animalAndBreed = petUseCases.getAnimalAndBreed(
            pet?.info?.animal ?: "",
            pet?.info?.breed ?: ""
        )
        val owners = petUseCases.getOwnersFromPet(petId)
        val currentOwnership = petUseCases.getCurrentUserPetOwnership(petId)
        val isPetFavorite = petUseCases.isPetFavorite(petId)
        val tag = petUseCases.getPetTag(pet?.info?.tag ?: "")
        updateState(ShowInfo(pet, animalAndBreed, owners, currentOwnership, isPetFavorite, tag))
    }

    private fun openOwnerProfile(owner: Owner) {
        // TODO
    }

    private fun requestAnimals() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val animals = petUseCases.getAllAnimals()
        updateState(HideLoading)
        updateEffect(ShowAnimalsList(animals))
    }

    private fun requestBreeds(animal: Animal) = safeLaunch(::showError) {
        updateState(ShowLoading)
        val breeds = petUseCases.getBreedsFromAnimal(animal.id)
        updateState(HideLoading)
        updateEffect(ShowBreedsList(breeds, animal))
    }

    private fun inviteOwner(owner: Owner) = safeLaunch(::showError) {
        petUseCases.inviteOwner(owner.user.id, petId, owner.category)
        updateEffect(ShowBottomMessage(R.string.invite_message, arrayOf(owner.user.info.name)))
    }

    private fun startPagingOwners(query: String) = timer.restart {
        if (checkNotValidQuery(query)) return@restart

        safeLaunch(::showError) {
            petUseCases.getOwnersToInvite(petId, query)
                .cachedIn(this)
                .collectLatest {
                updateState(ShowOwnersToInvite(it))
            }
        }
    }

    private fun toggleFavorite() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val isPetFavorite = petUseCases.isPetFavorite(petId)
        if (isPetFavorite == true) {
            petUseCases.removeFavorite(petId)
            updateState(DisableFavoritePet)
        } else {
            petUseCases.addFavorite(petId)
            updateState(EnableFavoritePet)
        }
    }

    private fun startScanner() {
        updateState(ShowScan)
    }

    private fun handleTag(tag: Tag?) {
        if (tag == null) {
            startScanner()
        } else {
            checkAvailability(tag)
        }
    }

    private fun checkAvailability(tag: Tag) {
        when (tag.info.available) {
            true -> updateState(ShowTagValid)
            false -> updateState(ShowTagNotAvailable)
        }
    }

    private fun checkNotValidQuery(query: String) = query.length < MIN_QUERY

    private fun showError(error: DefaultError) {
        updateState(HideLoading)
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
        data class ReportLost(val name: String, val lost: Boolean, val switchChecked: Boolean) : Action()
        data class ConfirmLost(val name: String) : Action()
        data class HandleTag(val tag: Tag?) : Action()
        object ToggleFavorite : Action()
        object OpenScanner : Action()
        object CancelLost : Action()
        object RequestAnimals : Action()
        object Refresh : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher

    companion object {
        private const val MIN_QUERY = 2
        private const val QUERY_DEBOUNCE_TIME = 200L
    }
}
