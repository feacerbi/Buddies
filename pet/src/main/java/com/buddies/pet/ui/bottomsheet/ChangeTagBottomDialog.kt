package com.buddies.pet.ui.bottomsheet

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.buddies.common.model.Tag
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.observe
import com.buddies.pet.R
import com.buddies.pet.databinding.ChangeTagDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class ChangeTagBottomDialog private constructor(
    private val bottomSheet: BottomSheetDialog,
    private val binding: ChangeTagDialogBinding,
    private val confirmAction: (String) -> Unit = {}
) {

    private var newTagId: String = ""

    fun enableConfirmButton(enable: Boolean) {
        binding.confirmButton.isEnabled = enable
    }

    fun setResult(message: String) {
        binding.qrScanner.setResultMessage(message)
    }

    fun show(fragment: Fragment, cameraHelper: CameraHelper, handleTag: (Tag?) -> Unit) = with (binding) {

        fragment.observe(qrScanner.scan(fragment, cameraHelper)) {
            newTagId = it?.id ?: ""
            handleTag.invoke(it)
        }

        confirmButton.setOnClickListener {
            confirmAction.invoke(newTagId)
            binding.qrScanner.stopScan()
            bottomSheet.cancel()
        }

        bottomSheet.show()
    }.let { this }

    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val changeView = ChangeTagDialogBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(changeView.root)

        private var confirmAction: (String) -> Unit = {}

        fun title(title: String) = with (changeView) {
            dialogTitle.text = title
        }.let { this }

        fun content(content: String) = with (changeView) {
            dialogContent.text = content
        }.let { this }

        fun cancelButton(
            title: String = changeView.root.context.resources.getString(R.string.cancel_button),
            action: () -> Unit = {}
        ) = with (changeView) {
            cancelButton.text = title
            cancelButton.setOnClickListener {
                action.invoke()
                qrScanner.stopScan()
                bottomSheet.cancel()
            }
        }.let { this }

        fun confirmButton(
            title: String = changeView.root.context.resources.getString(R.string.change_button),
            action: (String) -> Unit = {}
        ) = with (changeView) {
            confirmButton.text = title
            confirmAction = action
        }.let { this }

        fun build() = ChangeTagBottomDialog(bottomSheet, changeView, confirmAction).apply {
            bottomSheet.behavior.isFitToContents = true
        }
    }
}