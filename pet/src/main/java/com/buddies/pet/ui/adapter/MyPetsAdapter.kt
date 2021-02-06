package com.buddies.pet.ui.adapter

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Buddy
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.common.util.show
import com.buddies.pet.R
import com.buddies.pet.databinding.PetListItemBinding
import com.buddies.pet.ui.adapter.MyPetsAdapter.MyPetsViewHolder

class MyPetsAdapter(
    val owner: LifecycleOwner
) : ListAdapter<Buddy, MyPetsViewHolder>(BuddiesDiffUtil()) {

    var isBig: Boolean = false
    var onPetClick: ((Buddy) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPetsViewHolder =
        MyPetsViewHolder(
            PetListItemBinding.inflate(parent.inflater(), parent, false),
            onPetClick
        )

    override fun onBindViewHolder(holder: MyPetsViewHolder, position: Int) {
        holder.bind(getItem(position), isBig)
    }

    inner class MyPetsViewHolder(
        private val binding: PetListItemBinding,
        private val onClick: ((Buddy) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            buddy: Buddy,
            isBig: Boolean
        ) = with (binding) {

            root.layoutParams.width = if (isBig) MATCH_PARENT else WRAP_CONTENT
            root.setOnClickListener { onClick?.invoke(buddy) }

            val res = binding.root.context.resources
            val size = if (isBig) {
                res.getDimension(R.dimen.pet_icon_size_big)
            } else {
                res.getDimension(R.dimen.pet_icon_size_small)
            }

            petIcon.layoutParams.width = size.toInt()
            petIcon.layoutParams.height = size.toInt()
            petIcon.load(buddy.pet.info.photo, owner) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets
            }

            petName.text = buddy.pet.info.name
            petName.show = isBig

            ownershipLabel.text =
                binding.root.context.resources.getString(buddy.ownershipCategory.title)
            ownershipLabel.show = isBig

            space1.show = isBig
            space2.show = isBig
        }
    }

    private class BuddiesDiffUtil : DiffUtil.ItemCallback<Buddy>() {
        override fun areItemsTheSame(oldItem: Buddy, newItem: Buddy): Boolean =
            oldItem.pet.id == newItem.pet.id

        override fun areContentsTheSame(oldItem: Buddy, newItem: Buddy): Boolean =
            oldItem.pet.info == newItem.pet.info &&
                oldItem.ownershipCategory == newItem.ownershipCategory
    }
}