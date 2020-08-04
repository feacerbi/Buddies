package com.buddies.mypets.ui

import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.OwnershipCategory
import com.buddies.common.util.inflater
import com.buddies.mypets.databinding.EditOwnershipItemBinding
import com.buddies.mypets.ui.OwnershipsAdapter.OwnershipsViewHolder

class OwnershipsAdapter(
    selected: OwnershipCategory? = null,
    list: List<OwnershipCategory>? = OwnershipCategory.getCategoriesList(),
    private val onOwnershipChanged: ((OwnershipCategory) -> Unit)? = null
) : RecyclerView.Adapter<OwnershipsViewHolder>() {

    private val ownershipsList = mutableListOf<Pair<OwnershipCategory, Boolean>>()

    init {
        list?.forEach { category ->
            ownershipsList.add(Pair(category, category == selected))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnershipsViewHolder =
        OwnershipsViewHolder(
            EditOwnershipItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun getItemCount(): Int = ownershipsList.size

    override fun onBindViewHolder(holder: OwnershipsViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class OwnershipsViewHolder(
        private val binding: EditOwnershipItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val ownership = ownershipsList[position]

            checkIcon.isInvisible = ownership.second.not()
            checkedBackground.isVisible = ownership.second

            ownershipTitle.text = root.context.resources.getString(ownership.first.title)
            ownershipDescription.text = root.context.resources.getString(ownership.first.description)

            root.setOnClickListener {
                val oldIndex = ownershipsList.indexOfFirst { it.second }

                ownershipsList[oldIndex] = Pair(ownershipsList[oldIndex].first, false)
                ownershipsList[position] = Pair(ownership.first, true)

                notifyItemChanged(oldIndex)
                notifyItemChanged(position)

                onOwnershipChanged?.invoke(ownership.first)
            }
        }

    }
}