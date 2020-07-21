package com.buddies.mypets.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.buddies.common.model.OwnershipCategory
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.mypets.databinding.EditOwnershipListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OwnershipsBottomDialog(
    private val manager: FragmentManager
) : BottomSheetDialogFragment() {

    private lateinit var binding: EditOwnershipListBinding

    private var ownershipsAdapter = OwnershipsAdapter()
    private var currentSelectedOwnership = VISITOR
    private var onOwnershipChangedListener: ((OwnershipCategory) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = EditOwnershipListBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            applyButton.setOnClickListener {
                onOwnershipChangedListener?.invoke(currentSelectedOwnership)
                dismiss()
            }

            editOwnershipActions.adapter = ownershipsAdapter
        }
    }

    fun show(
        selected: OwnershipCategory,
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

        show(manager, null)
    }
}