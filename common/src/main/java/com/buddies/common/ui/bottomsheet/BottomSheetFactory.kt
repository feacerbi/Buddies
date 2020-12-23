package com.buddies.common.ui.bottomsheet

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BottomSheetFactory {

    protected fun createBottomSheet(contentView: View) =
        BottomSheetDialog(contentView.context).apply {
            setContentView(contentView)
            dismissWithAnimation = true
            setCanceledOnTouchOutside(true)
        }
}