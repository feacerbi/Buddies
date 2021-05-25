package com.buddies.missing_profile.viewstate

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
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
    val markAsButton: Boolean = false,
    @StringRes val markAsButtonText: Int = R.string.empty,
    @StringRes val typeDescription: Int = R.string.empty,
    val contactInfo: Boolean = false,
    @DrawableRes val contactInfoIcon: Int = R.drawable.ic_baseline_info,
    @MenuRes val toolbarMenu: Int = R.menu.empty_menu,
    val loading: Boolean = false
) : ViewState