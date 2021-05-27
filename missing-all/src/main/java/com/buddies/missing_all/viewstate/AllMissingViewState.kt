package com.buddies.missing_all.viewstate

import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewState
import com.buddies.missing_all.R

data class AllMissingViewState(
    @StringRes val searchHint: Int = R.string.empty,
    val allPets: PagingData<MissingPet> = PagingData.empty(),
    val showSorting: Boolean = true,
    val showSearch: Boolean = true,
    val showClear: Boolean = false
) : ViewState