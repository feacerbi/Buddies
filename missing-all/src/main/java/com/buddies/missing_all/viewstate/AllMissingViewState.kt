package com.buddies.missing_all.viewstate

import androidx.paging.PagingData
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewState

data class AllMissingViewState(
    val allPets: PagingData<MissingPet> = PagingData.empty(),
    val showSorting: Boolean = true,
    val showSearch: Boolean = true,
    val showClear: Boolean = false
) : ViewState