package com.buddies.pet.ui

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Breed
import com.buddies.common.ui.SelectableAdapter
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.pet.R
import com.buddies.pet.databinding.BreedListItemBinding
import com.buddies.pet.ui.BreedsAdapter.BreedsViewHolder

class BreedsAdapter(
    private val owner: LifecycleOwner,
    list: List<Breed>? = null,
    private var onBreedChanged: ((Breed) -> Unit)? = null
) : SelectableAdapter<BreedsViewHolder, Breed>() {

    private val breedsList = mutableListOf<Pair<Breed, Boolean>>()

    init {
        list?.forEach { breed ->
            breedsList.add(Pair(breed, false))
        }
    }

    fun getSelected() = breedsList.first { it.second }.first

    override fun addOnSelectedListener(listener: (Breed) -> Unit) {
        onBreedChanged = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedsViewHolder =
        BreedsViewHolder(
            BreedListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun getItemCount(): Int = breedsList.size

    override fun onBindViewHolder(holder: BreedsViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class BreedsViewHolder(
        private val binding: BreedListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val breed = breedsList[position]

            breedName.text = breed.first.breedInfo.name
            checkedBackground.isVisible = breed.second

            breedIcon.load(breed.first.breedInfo.photo, owner) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets_dark
            }

            root.setOnClickListener {
                val oldIndex = breedsList.indexOfFirst { it.second }

                if (oldIndex != -1) {
                    breedsList[oldIndex] = Pair(breedsList[oldIndex].first, false)
                    notifyItemChanged(oldIndex)
                }

                breedsList[position] = Pair(breed.first, true)
                notifyItemChanged(position)

                onBreedChanged?.invoke(breed.first)
            }
        }

    }
}