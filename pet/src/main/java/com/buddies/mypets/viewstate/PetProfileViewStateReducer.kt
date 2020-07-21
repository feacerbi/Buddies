package com.buddies.mypets.viewstate

import android.net.Uri
import androidx.core.net.toUri
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.model.Pet
import com.buddies.common.util.toOwnershipCategory
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.mypets.R

sealed class PetProfileViewStateReducer : ViewStateReducer<PetProfileViewState> {

    data class ShowInfo(
        val pet: Pet?,
        val ownersList: List<Owner>?,
        val currentOwnership: OwnershipInfo?
    ) : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            name = pet?.info?.name ?: ""
            nameEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            tag = pet?.info?.tag ?: ""
            tagEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            animal = pet?.info?.animal ?: ""
            animalEdit = currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL
            breed = pet?.info?.breed ?: ""
            photo = pet?.info?.photo?.toUri() ?: Uri.EMPTY
            toolbarMenu = if (currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL) {
                R.menu.pet_profile_toolbar_menu
            } else {
                R.menu.empty_menu
            }
            ownershipInfo = currentOwnership ?: OwnershipInfo()
            owners = ownersList ?: emptyList()
        }
    }

}