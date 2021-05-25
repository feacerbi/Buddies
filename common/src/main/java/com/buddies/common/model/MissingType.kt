package com.buddies.common.model

import androidx.annotation.StringRes
import com.buddies.common.R

enum class MissingType(@StringRes val description: Int) {
    LOST(R.string.missing_pet_lost_type),
    FOUND(R.string.missing_pet_found_type);

    companion object {
        fun fromName(name: String) = when (name) {
            LOST.name -> LOST
            FOUND.name -> FOUND
            else -> LOST
        }
    }
}