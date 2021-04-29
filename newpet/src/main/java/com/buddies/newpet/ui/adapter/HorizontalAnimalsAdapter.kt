package com.buddies.newpet.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Animal
import com.buddies.common.ui.adapter.SelectableAdapter
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.newpet.R
import com.buddies.newpet.databinding.AnimalHorizontalListItemBinding
import com.buddies.newpet.ui.adapter.HorizontalAnimalsAdapter.HorizontalAnimalsViewHolder

class HorizontalAnimalsAdapter(
    private val owner: LifecycleOwner,
    list: List<Animal>? = null,
    private var onAnimalChanged: ((Animal) -> Unit)? = null
) : SelectableAdapter<HorizontalAnimalsViewHolder, Animal>() {

    private val animalsList = mutableListOf<Pair<Animal, Boolean>>()

    init {
        list?.forEach { animal ->
            animalsList.add(Pair(animal, false))
        }
    }

    fun addItems(list: List<Animal>) {
        var notify = false

        list.forEach { animal ->
            if (animalsList.firstOrNull { it.first == animal } == null) {
                animalsList.add(Pair(animal, false))
                notify = true
            }
        }

        if (notify) notifyDataSetChanged()
    }

    override fun setOnSelectedListener(listener: (Animal) -> Unit) {
        onAnimalChanged = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalAnimalsViewHolder =
        HorizontalAnimalsViewHolder(
            AnimalHorizontalListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun getItemCount(): Int = animalsList.size

    override fun onBindViewHolder(holder: HorizontalAnimalsViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class HorizontalAnimalsViewHolder(
        private val binding: AnimalHorizontalListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val animal = animalsList[position]

            animalName.text = animal.first.animalInfo.name
            checkedBackground.isVisible = animal.second

            animalIcon.load(animal.first.animalInfo.photo, owner) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets_secondary
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