package com.buddies.missing_profile.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.DefaultError
import com.buddies.common.model.InfoType
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.navigation.Navigator.NavDirection.MissingPetToGallery
import com.buddies.common.util.LocationConverter
import com.buddies.common.util.safeLaunch
import com.buddies.common.util.toInfoType
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.contact.model.ContactInfo
import com.buddies.contact.model.ShareInfo
import com.buddies.missing_profile.R
import com.buddies.missing_profile.usecase.MissingPetUseCases
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeAnimal
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeName
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangePhoto
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeSharedInfo
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ConfirmPetRemoval
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ConfirmPetReturned
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.OpenGallery
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.PetReturned
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.Refresh
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RemovePet
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestAnimals
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestBreeds
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestContactInfo
import com.buddies.missing_profile.viewstate.MissingPetViewEffect
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.Navigate
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.NavigateBack
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowAnimalsList
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowBreedsList
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowConfirmRemovalBottomSheet
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowContactInfoBottomSheet
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowError
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowMessage
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowShareInfoBottomSheet
import com.buddies.missing_profile.viewstate.MissingPetViewState
import com.buddies.missing_profile.viewstate.MissingPetViewStateReducer.HideLoading
import com.buddies.missing_profile.viewstate.MissingPetViewStateReducer.ShowInfo
import com.buddies.missing_profile.viewstate.MissingPetViewStateReducer.ShowLoading
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class MissingPetProfileViewModel(
    private val petId: String,
    private val locationConverter: LocationConverter,
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
            is ChangeSharedInfo -> updateSharedInfo(action.contactInfo)
            is RequestBreeds -> requestBreeds(action.animal)
            is RequestAnimals -> requestAnimals()
            is RequestContactInfo -> requestContactInfo()
            is OpenGallery -> openGallery()
            is RemovePet -> removePet()
            is ConfirmPetRemoval -> confirmPetRemoval()
            is PetReturned -> returnedPet()
            is ConfirmPetReturned -> confirmPetReturned()
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

    private fun updateSharedInfo(list: List<ShareInfo>) = safeLaunch(::showError) {
        updateState(ShowLoading)
        missingPetUseCases.updatePetContactInfo(petId, list)
        updateLocation(list)
        refreshPet()
    }

    private suspend fun updateLocation(list: List<ShareInfo>) {
        val location = checkAndConvertLocation(list)
        val latitude = location?.first
        val longitude = location?.second

        if (latitude != null && longitude != null) {
            missingPetUseCases.updatePetLocation(petId, latitude, longitude)
        }
    }

    private fun removePet() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val petName = missingPetUseCases.getPet(petId)?.info?.name ?: ""
        updateEffect(ShowConfirmRemovalBottomSheet(R.string.confirm_removal_message, petName))
        updateState(HideLoading)
    }

    private fun confirmPetRemoval() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val petName = missingPetUseCases.getPet(petId)?.info?.name ?: ""
        missingPetUseCases.removePet(petId)
        updateEffect(ShowMessage(R.string.remove_confirmed_message, listOf(petName)))
        updateEffect(NavigateBack)
    }

    private fun returnedPet() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val pet = missingPetUseCases.getPet(petId)
        val petName = pet?.info?.name ?: ""
        val message = when (pet?.info?.type) {
            FOUND.name -> R.string.confirm_returned_message
            else -> R.string.confirm_found_message
        }
        updateEffect(MissingPetViewEffect.ShowConfirmReturnedBottomSheet(message, petName))
        updateState(HideLoading)
    }

    private fun confirmPetReturned() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val pet = missingPetUseCases.getPet(petId)
        val petName = pet?.info?.name ?: ""
        val message = when (pet?.info?.type) {
            FOUND.name -> R.string.returned_confirmed_message
            else -> R.string.found_confirmed_message
        }
        missingPetUseCases.markPetAsReturned(petId)
        updateEffect(ShowMessage(message, listOf(petName)))
        updateEffect(NavigateBack)
    }

    private fun refreshPet() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val pet = missingPetUseCases.getPet(petId)
        val animalAndBreed = missingPetUseCases.getAnimalAndBreed(
            pet?.info?.animal ?: "",
            pet?.info?.breed ?: ""
        )
        val reporter = missingPetUseCases.getUser(pet?.info?.reporter ?: "")
        val user = missingPetUseCases.getCurrentUser()
        updateState(ShowInfo(pet, animalAndBreed, reporter, user))
    }

    private fun requestAnimals() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val animals = missingPetUseCases.getAllAnimals()
        updateEffect(ShowAnimalsList(animals))
        updateState(HideLoading)
    }

    private fun requestBreeds(animal: Animal) = safeLaunch(::showError) {
        updateState(ShowLoading)
        val breeds = missingPetUseCases.getBreedsFromAnimal(animal.id)
        updateEffect(ShowBreedsList(breeds, animal))
        updateState(HideLoading)
    }

    private fun requestContactInfo() = safeLaunch(::showError) {
        updateState(ShowLoading)
        val pet = missingPetUseCases.getPet(petId)
        val user = missingPetUseCases.getCurrentUser()
        val contactInfo = pet?.info?.reporterInfo?.map {
            ContactInfo(it.key.toInfoType(), it.value)
        }

        if (pet?.info?.reporter == user?.id) {
            updateEffect(ShowShareInfoBottomSheet(contactInfo))
        } else {
            updateEffect(ShowContactInfoBottomSheet(contactInfo))
        }
        updateState(HideLoading)
    }

    private suspend fun checkAndConvertLocation(info: List<ShareInfo>) =
        info.find { it.type == InfoType.LOCATION }?.let {
            val location = locationConverter.geoPositionFromAddress(it.info)
            location.first to location.second
        }

    private fun openGallery() = safeLaunch(::showError) {
        val pet = missingPetUseCases.getPet(petId)
        val user = missingPetUseCases.getCurrentUser()
        val editable = pet?.info?.reporter == user?.id
        updateEffect(Navigate(MissingPetToGallery(petId, editable)))
    }

    private fun showError(error: DefaultError) {
        updateState(HideLoading)
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class ChangeName(val name: String) : Action()
        data class ChangeAnimal(val animal: Animal, val breed: Breed) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
        data class ChangeSharedInfo(val contactInfo: List<ShareInfo>) : Action()
        data class RequestBreeds(val animal: Animal) : Action()
        object RemovePet : Action()
        object ConfirmPetRemoval: Action()
        object PetReturned : Action()
        object ConfirmPetReturned : Action()
        object OpenGallery : Action()
        object RequestContactInfo : Action()
        object RequestAnimals : Action()
        object Refresh : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher
}
