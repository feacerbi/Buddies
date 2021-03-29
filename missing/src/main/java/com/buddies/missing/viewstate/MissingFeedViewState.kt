package com.buddies.missing.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.MissingPet
import com.buddies.common.viewstate.ViewState
import com.buddies.missing.R

data class MissingFeedViewState(
    val recentPets: List<MissingPet> = listOf(),
    val nearPets: List<MissingPet> = listOf(),
    val yourPets: List<MissingPet> = listOf(),
    val progress: Boolean = false,
    val titleName: String = "",
    @StringRes val title: Int = R.string.missing_page_title
) : ViewState