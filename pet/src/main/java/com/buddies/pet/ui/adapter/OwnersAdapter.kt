package com.buddies.pet.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.common.util.toOwnershipCategory
import com.buddies.pet.R
import com.buddies.pet.databinding.OwnerListItemBinding
import com.buddies.pet.ui.adapter.OwnersAdapter.OwnersViewHolder

class OwnersAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onClick: ((Owner) -> Unit)?,
    private val onOwnershipClick: ((Owner) -> Unit)?
) : ListAdapter<Owner, OwnersViewHolder>(OwnersDiffUtil()) {

    var currentOwnership: OwnershipInfo? = null

    override fun submitList(list: MutableList<Owner>?, commitCallback: Runnable?) {
        sortCurrentOwnerFirst(list)
        super.submitList(list, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnersViewHolder =
        OwnersViewHolder(
            OwnerListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: OwnersViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun sortCurrentOwnerFirst(list: MutableList<Owner>?) =
        list?.sortByDescending { it.user.id == currentOwnership?.userId }

    inner class OwnersViewHolder(
        private val binding: OwnerListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val owner = getItem(position)

            root.setOnClickListener { onClick?.invoke(owner) }

            ownerIcon.load(owner.user.info.photo, lifecycleOwner) {
                circleTransform = true
                error = R.drawable.ic_baseline_person
            }

            ownerName.text = owner.user.info.name
            ownerOwnership.text = root.context.resources.getString(owner.category.title)

            // TODO Move to a security class: safer, testable and avoids duplication
            editOwnership.isVisible =
                currentOwnership?.userId != null
                    && currentOwnership?.userId != owner.user.id
                    && currentOwnership?.category?.toOwnershipCategory()?.access == EDIT_ALL

            editOwnership.setOnClickListener {
                onOwnershipClick?.invoke(owner)
            }
        }
    }

    private class OwnersDiffUtil : DiffUtil.ItemCallback<Owner>() {
        override fun areItemsTheSame(oldItem: Owner, newItem: Owner): Boolean =
            oldItem.user.id == newItem.user.id

        override fun areContentsTheSame(oldItem: Owner, newItem: Owner): Boolean =
            oldItem.category == newItem.category
    }
}