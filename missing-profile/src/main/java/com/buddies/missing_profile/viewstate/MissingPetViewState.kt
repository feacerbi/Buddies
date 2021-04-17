package com.buddies.missing_profile.viewstate

import android.net.Uri
import androidx.annotation.MenuRes
import com.buddies.common.viewstate.ViewState
import com.buddies.missing_profile.R

data class MissingPetViewState(
    val name: String = "",
    val nameEdit: Boolean = false,
    val animal: String = "",
    val animalEdit: Boolean = false,
    val breed: String = "",
    val photo: Uri = Uri.EMPTY,
    val reporter: String = "",
    val contactInfo: Boolean = false,
    @MenuRes val toolbarMenu: Int = R.menu.empty_menu,
    val loading: Boolean = false
) : ViewState