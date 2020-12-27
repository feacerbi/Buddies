package com.buddies.common.util

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import com.buddies.common.R

class IntentActionSender {

    fun sendEmailAction(
        context: Context,
        email: String,
        @StringRes subject: Int = 0,
        @StringRes message: Int = 0
    ) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            if (subject != 0) putExtra(Intent.EXTRA_SUBJECT, subject)
            if (message != 0) putExtra(Intent.EXTRA_TEXT, message)
        }

        if (checkResolves(context, emailIntent)) {
            context.startActivity(emailIntent)
        } else {
            showToast(context, R.string.no_email_app_message)
        }
    }

    fun sendPhoneAction(
        context: Context,
        phone: String
    ) {
        val phoneUri = Uri.parse("tel:$phone")
        val phoneIntent = Intent(Intent.ACTION_DIAL, phoneUri)

        if (checkResolves(context, phoneIntent)) {
            context.startActivity(phoneIntent)
        } else {
            showToast(context, R.string.no_phone_app_message)
        }
    }

    fun sendLocationAction(
        context: Context,
        location: String
    ) {
        val locationUri = Uri.parse("geo:0,0?q=$location")
        val locationIntent = Intent(Intent.ACTION_VIEW, locationUri)

        if (checkResolves(context, locationIntent)) {
            context.startActivity(locationIntent)
        } else {
            showToast(context, R.string.no_map_app_message)
        }
    }

    private fun checkResolves(context: Context, intent: Intent): Boolean {
        val activities: List<ResolveInfo> = context.packageManager.queryIntentActivities(intent, 0)
        return activities.isNotEmpty()
    }

    private fun showToast(context: Context, @StringRes message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}