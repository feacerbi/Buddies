package com.buddies.missing.viewstate

import androidx.paging.PagingData
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewStateReducer

sealed class MissingFeedViewStateReducer : ViewStateReducer<MissingFeedViewState> {

    data class ShowPetLists(
        val name: String?,
        val mostRecentList: List<MissingPet>?,
        val yourList: List<MissingPet>?
    ) : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            recentPets = mostRecentList ?: listOf(),
            yourPets = yourList ?: listOf(),
            showYour = yourList?.isNotEmpty() ?: false,
            titleName = name?.substringBefore(" ") ?: "",
        )
    }

    data class ShowNearestPetsList(
        val nearestList: List<MissingPet>?,
    ) : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            nearPets = nearestList ?: listOf(),
            showNearYou = nearestList?.isNotEmpty() ?: false,
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
