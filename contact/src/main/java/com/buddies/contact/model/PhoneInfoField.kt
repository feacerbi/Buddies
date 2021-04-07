package com.buddies.contact.model

import android.content.Context
import com.buddies.common.model.InfoType.PHONE
import com.buddies.common.util.IntentActionSender
import com.buddies.contact.R

class PhoneInfoField(
    phone: String,
    callAction: (Context) -> Unit = {
        IntentActionSender().sendPhoneAction(
            it,
            phone
        )
    }
) : ContactInfoField(PHONE, 2, phone, R.drawable.ic_baseline_phone, R.drawable.ic_baseline_phone, callAction)
