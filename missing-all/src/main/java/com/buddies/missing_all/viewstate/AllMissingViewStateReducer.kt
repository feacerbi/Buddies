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
}
