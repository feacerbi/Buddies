package com.buddies.pet.viewstate

import android.net.Uri
import androidx.core.net.toUri
import androidx.paging.PagingData
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.model.Pet
import com.buddies.common.model.Tag
import com.buddies.common.util.toOwnershipCategory
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.pet.R

sealed class PetProfileViewStateReducer : ViewStateReducer<PetProfileViewState> {

    data class ShowInfo(
        val pet: Pet?,
        val animalAndBreed: Pair<Animal, Breed>?,
        val ownersList: List<Owner>?,
        val currentOwnership: OwnershipInfo?,
        val petTag: Tag?
    ) : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            name = pet?.info?.name ?: ""
            nameEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            tag = petTag?.info?.value ?: ""
            tagEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            animal = animalAndBreed?.first?.animalInfo?.name ?: ""
            animalEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            breed = animalAndBreed?.second?.breedInfo?.name ?: ""
            photo = pet?.info?.photo?.toUri() ?: Uri.EMPTY
            lost = pet?.info?.lost ?: false
            lostSwitch = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            lostStatus = if (pet?.info?.lost == true) R.string.pet_lost_status else R.string.pet_safe_status
            toolbarMenu = if (currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL) {
                R.menu.pet_profile_toolbar_menu
            } else {
                R.menu.empty_menu
            }
            ownershipInfo = currentOwnership ?: OwnershipInfo()
            owners = ownersList ?: emptyList()
            loading = false
        }
    }

    data class ShowOwnersToInvite(
        val data: PagingData<Owner>
    ) : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            pagingData = data
        }
    }

    data class ShowLoading(
        val show: Boolean = true
    ) : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            loading = show
        }
    }

    object ShowSafeStatus : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            lost = false
            lostStatus = R.string.pet_safe_status
        }
    }

    object ShowLostStatus : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            lost = true
            lostStatus = R.string.pet_lost_status
        }
    }
}