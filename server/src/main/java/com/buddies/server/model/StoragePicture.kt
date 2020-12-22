package com.buddies.server.model

import android.net.Uri
import com.google.android.gms.tasks.Task

data class StoragePicture(
    val id: String,
    val downloadTask: Task<Uri>
)
