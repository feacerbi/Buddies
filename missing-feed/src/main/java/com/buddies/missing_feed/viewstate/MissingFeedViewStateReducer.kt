package com.buddies.missing_feed.viewstate

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
            showRecents = mostRecentList?.isNotEmpty() ?: false,
            showYour = yourList?.isNotEmpty() ?: false,
            showEmptyList = mostRecentList?.isEmpty() ?: true && yourList?.isEmpty() ?: true && !state.showNearYou,
            titleName = name?.substringBefore(" ") ?: "",
            listsProgress = false,
            overallProgress = state.locationProgress
        )
    }

    data class ShowNearestPetsList(
        val nearestList: List<MissingPet>?,
    ) : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            nearPets = nearestList ?: listOf(),
            showNearYou = nearestList?.isNotEmpty() ?: false,
            showEmptyList = !state.showRecents && !state.showYour && nearestList?.isEmpty() ?: true,
            locationProgress = false,
            overallProgress = state.listsProgress
        )
    }

    object ShowLocationLoading : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            locationProgress = true
        )
    }

    object ShowLoading : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            listsProgress = true
        )
    }

    object HideLoading : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            listsProgress = false,
            locationProgress = false,
            overallProgress = false
        )
    }
}
