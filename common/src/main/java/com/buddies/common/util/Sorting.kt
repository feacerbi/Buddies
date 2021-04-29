package com.buddies.common.util

import com.buddies.common.R

enum class Sorting(val title: Int) {
    MOST_RECENT(R.string.sorting_most_recent),
    LEAST_RECENT(R.string.sorting_least_recent),
    NAME_AZ(R.string.sorting_name_az),
    NAME_ZA(R.string.sorting_name_za);

    companion object {
        fun fromName(name: String?) = when (name) {
            MOST_RECENT.name -> MOST_RECENT
            LEAST_RECENT.name -> LEAST_RECENT
            NAME_AZ.name -> NAME_AZ
            NAME_ZA.name -> NAME_ZA
            else -> MOST_RECENT
        }
    }
}