package com.buddies.missing.ui.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.MissingPet
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.missing.databinding.MissingPetVerticalItemBinding
import com.buddies.missing.ui.adapter.MissingPetsPagingAdapter.MissingPetViewHolder

class MissingPetsPagingAdapter(
    val lifecycleOwner: LifecycleOwner,
    val onPetClicked: ((MissingPet) -> Unit)? = null
) : PagingDataAdapter<MissingPet, MissingPetViewHolder>(MissingPetDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissingPetViewHolder =
        MissingPetViewHolder(
            MissingPetVerticalItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: MissingPetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MissingPetViewHolder(
        private val binding: MissingPetVerticalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            pet: MissingPet?
        ) = with (binding) {
            if (pet != null) {
                petPicture.load(pet.info.photo, lifecycleOwner)
                petName.text = pet.info.name
                petInfo.text = pet.info.animal

                root.setOnClickListener {
                    onPetClicked?.invoke(pet)
                }
            }
        }
    }

    private class MissingPetDiffUtil : DiffUtil.ItemCallback<MissingPet>() {

        override fun areItemsTheSame(oldItem: MissingPet, newItem: MissingPet): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MissingPet, newItem: MissingPet): Boolean =
            oldItem.info == newItem.info
    }
}