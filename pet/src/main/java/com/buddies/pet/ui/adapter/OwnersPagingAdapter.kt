package com.buddies.pet.ui.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.pet.R
import com.buddies.pet.databinding.OwnerInviteItemBinding
import com.buddies.pet.ui.adapter.OwnersPagingAdapter.OwnersPagingViewHolder

class OwnersPagingAdapter(
    private val lifecycleOwner: LifecycleOwner,
    var onClick: ((Owner) -> Unit)? = null
) : PagingDataAdapter<Owner, OwnersPagingViewHolder>(OwnersComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnersPagingViewHolder =
        OwnersPagingViewHolder(
            OwnerInviteItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: OwnersPagingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OwnersPagingViewHolder(
        private val binding: OwnerInviteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            owner: Owner?
        ) = with (binding) {
            if (owner != null) {

                ownerIcon.load(owner.user.info.photo, lifecycleOwner) {
                    circleTransform = true
                    error = R.drawable.ic_baseline_person
                }

                ownerName.text = owner.user.info.name
                inviteButton.isEnabled = owner.category == VISITOR

                inviteButton.text = root.context.getString(
                    if (owner.category == VISITOR) {
                        R.string.invite_button
                    } else {
                        owner.category.title
                    }
                )

                inviteButton.setOnClickListener { onClick?.invoke(owner) }
            }
        }
    }

    class OwnersComparator : DiffUtil.ItemCallback<Owner>() {

        override fun areItemsTheSame(oldItem: Owner, newItem: Owner): Boolean =
            oldItem.user.id == newItem.user.id

        override fun areContentsTheSame(oldItem: Owner, newItem: Owner): Boolean =
            oldItem.category == newItem.category
                && oldItem.user == newItem.user

    }
}