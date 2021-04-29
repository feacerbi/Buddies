package com.buddies.missing_all.viewstate

import androidx.paging.PagingData
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewStateReducer

sealed class AllMissingViewStateReducer : ViewStateReducer<AllMissingViewState> {

    data class ShowAllPets(
        val pets: PagingData<MissingPet>
    ) : AllMissingViewStateReducer() {
        override fun reduce(state: AllMissingViewState) = state.copy(
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
