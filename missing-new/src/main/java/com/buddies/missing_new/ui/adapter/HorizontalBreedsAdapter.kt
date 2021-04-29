package com.buddies.missing_new.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Breed
import com.buddies.common.ui.adapter.SelectableAdapter
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.missing_new.R
import com.buddies.missing_new.databinding.AnimalHorizontalListItemBinding
import com.buddies.missing_new.ui.adapter.HorizontalBreedsAdapter.HorizontalBreedsViewHolder

class HorizontalBreedsAdapter(
    private val owner: LifecycleOwner,
    list: List<Breed>? = null,
    private var onBreedChanged: ((Breed) -> Unit)? = null
) : SelectableAdapter<HorizontalBreedsViewHolder, Breed>() {

    private val breedsList = mutableListOf<Pair<Breed, Boolean>>()

    init {
        list?.forEach { breed ->
            breedsList.add(Pair(breed, false))
        }
    }

    fun setItems(list: List<Breed>?) {
        breedsList.clear()
        list?.forEach { breed ->
            breedsList.add(Pair(breed, false))
        }
        notifyDataSetChanged()
    }

    override fun setOnSelectedListener(listener: (Breed) -> Unit) {
        onBreedChanged = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalBreedsViewHolder =
        HorizontalBreedsViewHolder(
            AnimalHorizontalListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun getItemCount(): Int = breedsList.size

    override fun onBindViewHolder(holder: HorizontalBreedsViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class HorizontalBreedsViewHolder(
        private val binding: AnimalHorizontalListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val animal = breedsList[position]

            animalName.text = animal.first.breedInfo.name
            checkedBackground.isVisible = animal.second

            animalIcon.load(animal.first.breedInfo.photo, owner) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets_secondary
            }

            root.setOnClickListener {
                val oldIndex = breedsList.indexOfFirst { it.second }

                if (oldIndex != -1) {
                    breedsList[oldIndex] = Pair(breedsList[oldIndex].first, false)
                    notifyItemChanged(oldIndex)
                }

                breedsList[position] = Pair(animal.first, true)
                notifyItemChanged(position)

                onBreedChanged?.invoke(animal.first)
            }
        }

    }
}