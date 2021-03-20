package com.buddies.profile.ui.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.Pet
import com.buddies.common.model.PetFavorite
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.profile.R
import com.buddies.profile.databinding.FavoriteListItemBinding
import com.buddies.profile.ui.adapter.FavoritesAdapter.FavoriteViewHolder

class FavoritesAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val checkFavoriteAction: (PetFavorite) -> Unit,
    private val uncheckFavoriteAction: (PetFavorite) -> Unit,
    private val openPetProfileAction: ((Pet) -> Unit)? = null
) : PagingDataAdapter<PetFavorite, FavoriteViewHolder>(PetFavoriteDiffUtil()) {

    private val uncheckedSet: MutableSet<PetFavorite> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder =
        FavoriteViewHolder(
            FavoriteListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: FavoriteListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            favorite: PetFavorite?
        ) = with (binding) {
            if (favorite != null) {
                petName.text = favorite.pet.info.name
                petIcon.load(favorite.pet.info.photo, lifecycleOwner) {
                    circleTransform = true
                }

                favoriteButton.setImageResource(
                    if (uncheckedSet.contains(favorite)) {
                        R.drawable.ic_baseline_favorite_border
                    } else {
                        R.drawable.ic_baseline_favorite
                    }
                )

                favoriteButton.setOnClickListener {
                    if (uncheckedSet.contains(favorite)) {
                        uncheckedSet.remove(favorite)
                        checkFavoriteAction.invoke(favorite)
                    } else {
                        uncheckedSet.add(favorite)
                        uncheckFavoriteAction.invoke(favorite)
                    }
                    notifyItemChanged(bindingAdapterPosition)
                }

                root.setOnClickListener {
                    openPetProfileAction?.invoke(favorite.pet)
                }
            }
        }
    }

    class PetFavoriteDiffUtil : DiffUtil.ItemCallback<PetFavorite>() {
        override fun areItemsTheSame(oldItem: PetFavorite, newItem: PetFavorite): Boolean =
            oldItem.favoriteId == newItem.favoriteId

        override fun areContentsTheSame(oldItem: PetFavorite, newItem: PetFavorite): Boolean =
            oldItem.pet == newItem.pet
    }
}