package com.buddies.pet.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.buddies.common.model.OwnershipCategory
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.pet.R
import com.buddies.pet.databinding.EditOwnershipDialogBinding
import com.buddies.pet.ui.adapter.OwnershipsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OwnershipsBottomDialog(
    private val manager: FragmentManager
) : BottomSheetDialogFragment() {

    private lateinit var binding: EditOwnershipDialogBinding

    private var ownershipsAdapter = OwnershipsAdapter()
    private var currentSelectedOwnership = VISITOR
    private var onOwnershipChangedListener: ((OwnershipCategory) -> Unit)? = null
    @StringRes private var applyButtonText = R.string.apply_button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = EditOwnershipDialogBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            applyButton.text = getString(applyButtonText)
            applyButton.setOnClickListener {
                onOwnershipChangedListener?.invoke(currentSelectedOwnership)
                dismiss()
            }

            cancelButton.setOnClickListener {
                dismiss()
            }

            editOwnershipActions.adapter = ownershipsAdapter
        }
    }

    fun show(
        selected: OwnershipCategory,
        @StringRes positiveButtonText: Int = applyButtonText,
        onOwnershipChanged: ((OwnershipCategory) -> Unit)? = null
    ) {
        ownershipsAdapter = OwnershipsAdapter(selected) {
            currentSelectedOwnership = it
            onOwnershipChangedListener = if (currentSelectedOwnership != selected) {
                onOwnershipChanged
            } else {
                null
            }
        }

        applyButtonText = positiveButtonText

        show(manager, null)
    }
}