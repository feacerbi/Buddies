package com.buddies.mypets.model

import androidx.recyclerview.widget.DiffUtil
import com.buddies.common.model.Owner

object OwnersComparator : DiffUtil.ItemCallback<Owner>() {

    override fun areItemsTheSame(oldItem: Owner, newItem: Owner): Boolean =
        oldItem.user.id == newItem.user.id

    override fun areContentsTheSame(oldItem: Owner, newItem: Owner): Boolean =
        oldItem.category == newItem.category
                && oldItem.user == newItem.user

}