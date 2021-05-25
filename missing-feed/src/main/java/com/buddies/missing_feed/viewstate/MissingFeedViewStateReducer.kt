package com.buddies.missing_feed.viewstate

import com.buddies.common.model.MissingPet
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.common.viewstate.ViewStateReducer

sealed class MissingFeedViewStateReducer : ViewStateReducer<MissingFeedViewState> {

    data class ShowPetLists(
        val name: String?,
        val mostRecentList: List<MissingPet>?,
        val yourList: List<MissingPet>?
    ) : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            recentLostPets = mostRecentList?.filter { it.info.type == LOST.name } ?: listOf(),
            yourLostPets = yourList?.filter { it.info.type == LOST.name } ?: listOf(),
            showLostRecents = mostRecentList?.any { it.info.type == LOST.name } ?: false,
            showLostYour = yourList?.any { it.info.type == LOST.name } ?: false,
            showLostEmptyList = !state.showLostNearYou
                && mostRecentList?.all { it.info.type != LOST.name } ?: true
                && yourList?.all { it.info.type != LOST.name } ?: true,
            recentFoundPets = mostRecentList?.filter { it.info.type == FOUND.name } ?: listOf(),
            yourFoundPets = yourList?.filter { it.info.type == FOUND.name } ?: listOf(),
            showFoundRecents = mostRecentList?.any { it.info.type == FOUND.name } ?: false,
            showFoundYour = yourList?.any { it.info.type == FOUND.name } ?: false,
            showFoundEmptyList = !state.showFoundNearYou
                && mostRecentList?.all { it.info.type != FOUND.name } ?: true
                && yourList?.all { it.info.type != FOUND.name } ?: true,
            titleName = name?.substringBefore(" ") ?: "",
            listsProgress = false,
            overallProgress = state.locationProgress
        )
    }

    data class ShowNearestPetsList(
        val nearestList: List<MissingPet>?,
    ) : MissingFeedViewStateReducer() {
        override fun reduce(state: MissingFeedViewState) = state.copy(
            nearLostPets = nearestList?.filter { it.info.type == LOST.name } ?: listOf(),
            showLostNearYou = nearestList?.any { it.info.type == LOST.name } ?: false,
            showLostEmptyList = nearestList?.all { it.info.type != LOST.name } ?: true
                && !state.showLostRecents
                && !state.showLostYour,
            nearFoundPets = nearestList?.filter { it.info.type == FOUND.name } ?: listOf(),
            showFoundNearYou = nearestList?.any { it.info.type == FOUND.name } ?: false,
            showFoundEmptyList = nearestList?.all { it.info.type != FOUND.name } ?: true
                && !state.showLostRecents
                && !state.showLostYour,
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
