package com.buddies.common.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionsManager(
    private val context: Context
) {

    fun requestCameraPermissions(activity: Activity) =
        ActivityCompat.requestPermissions(
                    activity, REQUIRED_CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSIONS
        )

    fun requestCameraPermissions(fragment: Fragment) =
                fragment.requestPermissions(REQUIRED_CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSIONS)

    fun onRequestResult(
        requestCode: Int,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {
            if (cameraPermissionsGranted()) {
                onSuccess.invoke()
            } else {
                onFail.invoke(REQUIRED_CAMERA_PERMISSIONS.joinToString(separator = ", "))
            }
        }
    }

    fun cameraPermissionsGranted() = REQUIRED_CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
    }
}