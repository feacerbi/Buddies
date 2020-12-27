package com.buddies.profile.model

import android.content.Context
import com.buddies.common.model.ShareInfoType.EMAIL
import com.buddies.common.util.IntentActionSender
import com.buddies.profile.R

class EmailInfoField(
    email: String,
    sendAction: (Context) -> Unit = {
        IntentActionSender().sendEmailAction(
            it,
            email,
            R.string.email_action_subject,
            R.string.email_action_message
        )
    }
) : ContactInfoField(EMAIL, 1, email, R.drawable.ic_baseline_email, R.drawable.ic_baseline_email, sendAction)
