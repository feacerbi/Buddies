package com.buddies.scanner.ui

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.buddies.common.model.Breed
import com.buddies.common.ui.SelectableAdapter
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.inflater
import com.buddies.scanner.R
import com.buddies.scanner.databinding.AnimalHorizontalListItemBinding
import com.buddies.scanner.ui.HorizontalBreedsAdapter.HorizontalBreedsViewHolder

class HorizontalBreedsAdapter(
    list: List<Breed>? = null,
    private val owner: LifecycleOwner? = null,
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

    fun getSelected() = breedsList.first { it.second }.first

    override fun addOnSelectedListener(listener: (Breed) -> Unit) {
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

            animalIcon.load(animal.first.breedInfo.photo) {
                createLoadRequest(owner,  true, R.drawable.ic_baseline_pets)
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