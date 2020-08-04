package com.buddies.mypets.ui

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.buddies.common.model.Animal
import com.buddies.common.ui.SelectableAdapter
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.inflater
import com.buddies.mypets.R
import com.buddies.mypets.databinding.AnimalListItemBinding
import com.buddies.mypets.ui.AnimalsAdapter.AnimalsViewHolder

class AnimalsAdapter(
    list: List<Animal>? = null,
    private val owner: LifecycleOwner? = null,
    private var onAnimalChanged: ((Animal) -> Unit)? = null
) : SelectableAdapter<AnimalsViewHolder, Animal>() {

    private val animalsList = mutableListOf<Pair<Animal, Boolean>>()

    init {
        list?.forEach { animal ->
            animalsList.add(Pair(animal, false))
        }
    }

    fun getSelected() = animalsList.first { it.second }.first

    override fun addOnSelectedListener(listener: (Animal) -> Unit) {
        onAnimalChanged = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder =
        AnimalsViewHolder(
            AnimalListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun getItemCount(): Int = animalsList.size

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class AnimalsViewHolder(
        private val binding: AnimalListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val animal = animalsList[position]

            animalName.text = animal.first.animalInfo.name
            checkedBackground.isVisible = animal.second

            animalIcon.load(animal.first.animalInfo.photo) {
                createLoadRequest(owner,  true, R.drawable.ic_baseline_pets_dark)
            }

            root.setOnClickListener {
                val oldIndex = animalsList.indexOfFirst { it.second }

                if (oldIndex != -1) {
                    animalsList[oldIndex] = Pair(animalsList[oldIndex].first, false)
                    notifyItemChanged(oldIndex)
                }

                animalsList[position] = Pair(animal.first, true)
                notifyItemChanged(position)

                onAnimalChanged?.invoke(animal.first)
            }
        }

    }
}