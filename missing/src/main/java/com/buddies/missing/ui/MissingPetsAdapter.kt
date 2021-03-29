package com.buddies.missing.ui

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.MissingPet
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.missing.databinding.MissingPetItemBinding
import com.buddies.missing.ui.MissingPetsAdapter.MissingPetViewHolder

class MissingPetsAdapter(
    val lifecycleOwner: LifecycleOwner
) : ListAdapter<MissingPet, MissingPetViewHolder>(MissingPetDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissingPetViewHolder =
        MissingPetViewHolder(
            MissingPetItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: MissingPetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MissingPetViewHolder(
        private val binding: MissingPetItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            pet: MissingPet
        ) = with (binding) {
            petPicture.load(pet.info.photo, lifecycleOwner)
            petName.text = pet.info.name
            petInfo.text = pet.info.animal
        }
    }

    private class MissingPetDiffUtil : DiffUtil.ItemCallback<MissingPet>() {

        override fun areItemsTheSame(oldItem: MissingPet, newItem: MissingPet): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MissingPet, newItem: MissingPet): Boolean =
            oldItem.info == newItem.info
    }
}