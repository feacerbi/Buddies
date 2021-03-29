package com.buddies.missing.viewstate

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
            titleName = name?.substringBefore(" ") ?: "",
        )
    }
}
