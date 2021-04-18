package com.buddies.missing.viewstate

import androidx.paging.PagingData
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewStateReducer

sealed class MissingFeedViewStateReducer : ViewStateReducer<MissingFeedViewState> {

    data class ShowPetLists(
        val name: String?,
        val mostRecentList: List<MissingPet>?,
        val nearestList: List<MissingPet>?,
        val yourList: List<MissingPet>?
    ) : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            recentPets = mostRecentList ?: listOf(),
            nearPets = nearestList ?: listOf(),
            yourPets = yourList ?: listOf(),
            showNearYou = nearestList?.isNotEmpty() ?: false,
            showYour = yourList?.isNotEmpty() ?: false,
            titleName = name?.substringBefore(" ") ?: "",
        )
    }

    data class ShowAllPets(
        val pets: PagingData<MissingPet>
    ) : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            allPets = pets
        )
    }
}
