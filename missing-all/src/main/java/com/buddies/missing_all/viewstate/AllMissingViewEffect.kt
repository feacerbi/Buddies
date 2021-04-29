package com.buddies.missing_all.viewstate

import androidx.annotation.StringRes
import com.buddies.common.navigation.Navigator
import com.buddies.common.util.Sorting
import com.buddies.common.viewstate.ViewEffect

sealed class AllMissingViewEffect : ViewEffect {
    data class ShowSortingDialog(val currentSorting: Sorting) : AllMissingViewEffect()
    data class ShowMessage(@StringRes val message: Int) : AllMissingViewEffect()
    data class Navigate(val direction: Navigator.NavDirection) : AllMissingViewEffect()
    data class ShowError(@StringRes val error: Int) : AllMissingViewEffect()
}
