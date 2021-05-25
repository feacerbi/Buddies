package com.buddies.missing_feed.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewState
import com.buddies.missing_feed.R

data class MissingFeedViewState(
    val recentLostPets: List<MissingPet> = listOf(),
    val nearLostPets: List<MissingPet> = listOf(),
    val yourLostPets: List<MissingPet> = listOf(),
    val recentFoundPets: List<MissingPet> = listOf(),
    val nearFoundPets: List<MissingPet> = listOf(),
    val yourFoundPets: List<MissingPet> = listOf(),
    val showLostRecents: Boolean = false,
    val showLostNearYou: Boolean = false,
    val showLostYour: Boolean = false,
    val showFoundRecents: Boolean = false,
    val showFoundNearYou: Boolean = false,
    val showFoundYour: Boolean = false,
    val showLostEmptyList: Boolean = true,
    val showFoundEmptyList: Boolean = true,
    val listsProgress: Boolean = false,
    val locationProgress: Boolean = false,
    val overallProgress: Boolean = false,
    val titleName: String = "",
    @StringRes val title: Int = R.string.missing_page_title
) : ViewState