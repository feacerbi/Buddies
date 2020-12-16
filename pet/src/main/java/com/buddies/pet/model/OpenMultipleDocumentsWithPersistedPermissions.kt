package com.buddies.pet.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class OpenMultipleDocumentsWithPersistedPermissions(
    private val applicationContext: Context
) : ActivityResultContract<Array<String>, MutableList<Uri>>() {

    private val openMultipleDocuments by lazy {
        ActivityResultContracts.OpenMultipleDocuments()
    }

    override fun createIntent(context: Context, input: Array<String>): Intent {
        return openMultipleDocuments.createIntent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): MutableList<Uri> {
        return openMultipleDocuments.parseResult(resultCode, intent).onEach {
            applicationContext.contentResolver
                .takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}