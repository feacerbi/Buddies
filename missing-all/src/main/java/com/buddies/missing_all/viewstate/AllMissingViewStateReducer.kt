package com.buddies.missing_all.viewstate

import androidx.paging.PagingData
import com.buddies.common.model.MissingPet
import com.buddies.common.model.MissingType
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.missing_all.R

sealed class AllMissingViewStateReducer : ViewStateReducer<AllMissingViewState> {

    data class ShowAllPets(
        val missingType: MissingType,
        val pets: PagingData<MissingPet>
    ) : AllMissingViewStateReducer() {
        override fun reduce(state: AllMissingViewState) = state.copy(
            searchHint = when (missingType) {
                LOST -> R.string.search_lost_pets_hint
                FOUND -> R.string.search_found_pets_hint
            },
            allPets = pets
        )
    }

    object ShowSorting : AllMissingViewStateReducer() {
        override fun reduce(state: AllMissingViewState) = state.copy(
            showSorting = true,
            showSearch = true,
            showClear = false
        )
    }

    object HideSorting : AllMissingViewStateReducer() {
        override fun reduce(state: AllMissingViewState) = state.copy(
            showSorting = false,
            showSearch = false,
            showClear = true
        )
    }

}
