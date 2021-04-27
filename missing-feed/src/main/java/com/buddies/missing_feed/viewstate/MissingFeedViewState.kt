package com.buddies.missing_feed.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewState
import com.buddies.missing_feed.R

data class MissingFeedViewState(
    val recentPets: List<MissingPet> = listOf(),
    val nearPets: List<MissingPet> = listOf(),
    val yourPets: List<MissingPet> = listOf(),
    val showRecents: Boolean = false,
    val showNearYou: Boolean = false,
    val showYour: Boolean = false,
    val showEmptyList: Boolean = true,
    val listsProgress: Boolean = false,
    val locationProgress: Boolean = false,
    val overallProgress: Boolean = false,
    val titleName: String = "",
    @StringRes val title: Int = R.string.missing_page_title
) : ViewState