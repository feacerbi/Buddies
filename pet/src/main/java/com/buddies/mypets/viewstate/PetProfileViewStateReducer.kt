package com.buddies.mypets.viewstate

import android.net.Uri
import androidx.core.net.toUri
import androidx.paging.PagingData
import com.buddies.common.model.*
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.util.toOwnershipCategory
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.mypets.R

sealed class PetProfileViewStateReducer : ViewStateReducer<PetProfileViewState> {

    data class ShowInfo(
        val pet: Pet?,
        val animalAndBreed: Pair<Animal, Breed>?,
        val ownersList: List<Owner>?,
        val currentOwnership: OwnershipInfo?
    ) : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            name = pet?.info?.name ?: ""
            nameEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            tag = pet?.info?.tag ?: ""
            tagEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            animal = animalAndBreed?.first?.animalInfo?.name ?: ""
            animalEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            breed = animalAndBreed?.second?.breedInfo?.name ?: ""
            photo = pet?.info?.photo?.toUri() ?: Uri.EMPTY
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

    data class Loading(
        val show: Boolean = true
    ) : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            loading = show
        }
    }
}