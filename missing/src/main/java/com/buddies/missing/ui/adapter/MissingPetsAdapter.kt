package com.buddies.missing.ui.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.MissingPet
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.missing.databinding.MissingPetHorizontalItemBinding
import com.buddies.missing.ui.adapter.MissingPetsAdapter.MissingPetViewHolder

class MissingPetsAdapter(
    val lifecycleOwner: LifecycleOwner,
    val onPetClicked: ((MissingPet) -> Unit)? = null
) : ListAdapter<MissingPet, MissingPetViewHolder>(MissingPetDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissingPetViewHolder =
        MissingPetViewHolder(
            MissingPetHorizontalItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: MissingPetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MissingPetViewHolder(
        private val binding: MissingPetHorizontalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            pet: MissingPet
        ) = with (binding) {
            petPicture.load(pet.info.photo, lifecycleOwner)
            petName.text = pet.info.name
            petInfo.text = pet.info.animal

            root.setOnClickListener {
                onPetClicked?.invoke(pet)
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