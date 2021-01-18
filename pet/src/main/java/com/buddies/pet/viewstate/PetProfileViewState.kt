package com.buddies.pet.viewstate

import android.net.Uri
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.viewstate.ViewState
import com.buddies.pet.R

data class PetProfileViewState(
    val name: String = "",
    val nameEdit: Boolean = false,
    val tag: String = "",
    val tagEdit: Boolean = false,
    val tagValid: Boolean = false,
    @StringRes val tagResult: Int = R.string.empty,
    val animal: String = "",
    val animalEdit: Boolean = false,
    val breed: String = "",
    val photo: Uri = Uri.EMPTY,
    val lost: Boolean = false,
    val lostSwitch: Boolean = false,
    @StringRes val lostStatus: Int = R.string.empty,
    @MenuRes val toolbarMenu: Int = R.menu.empty_menu,
    val ownershipInfo: OwnershipInfo = OwnershipInfo(),
    val owners: List<Owner> = emptyList(),
    val pagingData: PagingData<Owner> = PagingData.empty(),
    val loading: Boolean = false
) : ViewState