package com.buddies.generator.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class ClipboardHelper(
    private val appContext: Context
) {

    fun saveToClipboard(text: String, label: String = DEFAULT_LABEL) {
        val clipboardService = appContext.getSystemService(Context.CLIPBOARD_SERVICE)

        if (clipboardService is ClipboardManager) {
            clipboardService.setPrimaryClip(ClipData.newPlainText(label, text))
        }
    }

    fun pasteFromClipboard(text: String, label: String = DEFAULT_LABEL): String {
        val clipboardService = appContext.getSystemService(Context.CLIPBOARD_SERVICE)

        if (clipboardService is ClipboardManager) {
            val primaryClip = clipboardService.primaryClip

            if (primaryClip != null && primaryClip.itemCount > 0) {
                return primaryClip.getItemAt(0).text?.toString() ?: ""
            }
        }

        return ""
    }

    companion object {
        private const val DEFAULT_LABEL = "Buddies"
    }
}