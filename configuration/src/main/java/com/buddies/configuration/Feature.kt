package com.buddies.configuration

import androidx.annotation.StringRes

enum class Feature(@StringRes val key: Int) {
    MY_PETS(R.string.show_my_pets_key),
    PET_SCANNER(R.string.show_pet_scanner_key)
}