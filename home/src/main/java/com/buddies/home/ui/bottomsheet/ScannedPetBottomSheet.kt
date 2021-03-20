package com.buddies.home.ui.bottomsheet

import android.net.Uri
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.common.util.load
import com.buddies.home.R
import com.buddies.home.databinding.ScanResultLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ScannedPetBottomSheet private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val scannedView = ScanResultLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(scannedView.root)

        private val confirmButtonDefaultText by lazy {
            scannedView.root.context.resources.getString(R.string.ok_button)
        }
        private val cancelButtonDefaultText by lazy {
            scannedView.root.context.resources.getString(R.string.cancel_button)
        }

        fun title(title: String) = with (scannedView) {
            dialogTitle.text = title
        }.let { this }

        fun content(content: String) = with (scannedView) {
            dialogContent.text = content
        }.let { this }

        fun petIcon(icon: String, lifecycleOwner: LifecycleOwner) = with (scannedView) {
            petIcon.load(Uri.parse(icon), lifecycleOwner) {
                circleTransform = true
            }
        }.let { this }

        fun petName(name: String) = with (scannedView) {
            petName.text = name
        }.let { this }

        fun petStatus(lost: Boolean) = with (scannedView) {
            petStatus.text = scannedView.root.context.resources.getString(
                if (lost) R.string.pet_status_lost else R.string.pet_status_safe
            )
        }.let { this }

        fun cancelButton(
            title: String = cancelButtonDefaultText,
            action: () -> Unit = {}
        ) = with (scannedView) {
            cancelButton.text = title
            cancelButton.setOnClickListener {
                bottomSheet.cancel()
                action.invoke()
            }
        }.let { this }

        fun confirmButton(
            title: String = confirmButtonDefaultText,
            action: () -> Unit = {}
        ) = with (scannedView) {
            confirmButton.text = title
            confirmButton.setOnClickListener {
                bottomSheet.cancel()
                action.invoke()
            }
        }.let { this }

        fun build() = ScannedPetBottomSheet(bottomSheet)
    }
}