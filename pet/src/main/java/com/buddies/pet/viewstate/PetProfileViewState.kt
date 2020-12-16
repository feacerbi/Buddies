package com.buddies.pet.viewstate

import android.net.Uri
import androidx.annotation.MenuRes
import androidx.paging.PagingData
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.viewstate.ViewState
import com.buddies.pet.R

class PetProfileViewState(
    var name: String = "",
    var nameEdit: Boolean = false,
    var tag: String = "",
    var tagEdit: Boolean = false,
    var animal: String = "",
    var animalEdit: Boolean = false,
    var breed: String = "",
    var photo: Uri = Uri.EMPTY,
    @MenuRes var toolbarMenu: Int = R.menu.empty_menu,
    var ownershipInfo: OwnershipInfo = OwnershipInfo(),
    var owners: List<Owner> = emptyList(),
    var pagingData: PagingData<Owner> = PagingData.empty(),
    var gallery: List<Uri> = listOf(),
    var loading: Boolean = false
) : ViewState