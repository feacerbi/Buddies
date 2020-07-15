package com.buddies.mypets.viewstate

import android.net.Uri
import androidx.core.net.toUri
import com.buddies.common.model.Pet
import com.buddies.common.viewstate.ViewStateReducer

sealed class PetProfileViewStateReducer : ViewStateReducer<PetProfileViewState> {

    data class ShowInfo(val pet: Pet?) : PetProfileViewStateReducer() {
        override val reduce: PetProfileViewState.() -> Unit = {
            name = pet?.info?.name ?: ""
            tag = pet?.info?.tag ?: ""
            animal = pet?.info?.animal ?: ""
            breed = pet?.info?.breed ?: ""
            photo = pet?.info?.photo?.toUri() ?: Uri.EMPTY
        }
    }

}