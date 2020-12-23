package com.buddies.common.ui.bottomsheet

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomBottomSheet private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    fun cancel(action: () -> Unit = {}) {
        bottomSheet.setOnCancelListener { action.invoke() }
        bottomSheet.cancel()
    }

    class Builder(
        customView: View
    ) : BottomSheetFactory() {

        private val bottomSheet = createBottomSheet(customView)

        fun build() = CustomBottomSheet(bottomSheet)
    }
}