package com.buddies.mypets.ui

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.common.util.toOwnershipCategory
import com.buddies.mypets.R
import com.buddies.mypets.databinding.OwnerListItemBinding
import com.buddies.mypets.ui.OwnersAdapter.OwnersViewHolder

class OwnersAdapter(
    private val lifecycleOwner: LifecycleOwner,
    owners: List<Owner>? = null,
    private val currentOwnership: OwnershipInfo,
    private val onClick: ((Owner) -> Unit)?,
    private val onOwnershipClick: ((Owner) -> Unit)?
) : RecyclerView.Adapter<OwnersViewHolder>() {

    private val ownersList = mutableListOf<Owner>()

    init {
        if (owners != null) ownersList.addAll(owners)
        sortCurrentOwnerFirst()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnersViewHolder =
        OwnersViewHolder(
            OwnerListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun getItemCount(): Int = ownersList.size

    override fun onBindViewHolder(holder: OwnersViewHolder, position: Int) {
        holder.bind(ownersList[position])
    }

    private fun sortCurrentOwnerFirst() =
        ownersList.sortByDescending { it.user.id == currentOwnership.userId }

    inner class OwnersViewHolder(
        private val binding: OwnerListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            owner: Owner
        ) = with (binding) {
            root.setOnClickListener { onClick?.invoke(owner) }

            ownerIcon.load(owner.user.info.photo, lifecycleOwner) {
                circleTransform = true
                error = R.drawable.ic_baseline_person
            }

            ownerName.text = owner.user.info.name
            ownerOwnership.text = root.context.resources.getString(owner.category.title)

            editOwnership.isVisible =
                owner.user.id != currentOwnership.userId
                        && currentOwnership.category.toOwnershipCategory().access == EDIT_ALL

            editOwnership.setOnClickListener {
                onOwnershipClick?.invoke(owner)
            }
        }
    }
}