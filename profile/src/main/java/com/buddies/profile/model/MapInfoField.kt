package com.buddies.profile.model

import android.content.Context
import com.buddies.common.model.ShareInfoType.LOCATION
import com.buddies.common.util.IntentActionSender
import com.buddies.profile.R

class MapInfoField(
    location: String,
    openAction: (Context) -> Unit = {
        IntentActionSender().sendLocationAction(
            it,
            location
        )
    }
) : ContactInfoField(LOCATION, 3, location, R.drawable.ic_baseline_location, R.drawable.ic_baseline_map, openAction)
