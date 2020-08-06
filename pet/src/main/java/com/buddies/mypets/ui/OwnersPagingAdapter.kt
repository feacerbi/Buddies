package com.buddies.mypets.ui

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.inflater
import com.buddies.mypets.R
import com.buddies.mypets.databinding.OwnerInviteItemBinding
import com.buddies.mypets.ui.OwnersPagingAdapter.OwnersPagingViewHolder

class OwnersPagingAdapter(
    diffCallback: DiffUtil.ItemCallback<Owner>,
    private val lifecycleOwner: LifecycleOwner? = null,
    var onClick: ((Owner) -> Unit)? = null
) : PagingDataAdapter<Owner, OwnersPagingViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnersPagingViewHolder =
        OwnersPagingViewHolder(
            OwnerInviteItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: OwnersPagingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OwnersPagingViewHolder(
        val binding: OwnerInviteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            owner: Owner?
        ) = with (binding) {
            if (owner != null) {

                ownerIcon.load(owner.user.info.photo) {
                    createLoadRequest(lifecycleOwner,  true, R.drawable.ic_baseline_person)
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
}