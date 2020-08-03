package com.buddies.mypets.ui

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.buddies.common.model.Pet
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.inflater
import com.buddies.common.util.show
import com.buddies.mypets.R
import com.buddies.mypets.databinding.PetListItemBinding
import com.buddies.mypets.ui.MyPetsAdapter.MyPetsViewHolder

class MyPetsAdapter(
    pets: List<Pet>? = null,
    var isBig: Boolean = false,
    var onPetClick: ((Pet) -> Unit)? = null,
    private val owner: LifecycleOwner? = null
) : RecyclerView.Adapter<MyPetsViewHolder>() {

    private val petsList = mutableListOf<Pet>()

    init {
        if (pets != null) petsList.addAll(pets)
    }

    fun setItems(pets: List<Pet>?) {
        if (pets != null) {
            petsList.clear()
            petsList.addAll(pets)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPetsViewHolder =
        MyPetsViewHolder(
            PetListItemBinding.inflate(parent.inflater(), parent, false),
            onPetClick
        )

    override fun getItemCount(): Int = petsList.size

    override fun onBindViewHolder(holder: MyPetsViewHolder, position: Int) {
        holder.bind(petsList[position], isBig, owner)
    }

    class MyPetsViewHolder(
        private val binding: PetListItemBinding,
        private val onClick: ((Pet) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            pet: Pet,
            isBig: Boolean,
            owner: LifecycleOwner?
        ) = with (binding) {

            root.layoutParams.width = if (isBig) MATCH_PARENT else WRAP_CONTENT
            root.setOnClickListener { onClick?.invoke(pet) }

            val res = binding.root.context.resources
            val size = if (isBig) {
                res.getDimension(R.dimen.pet_icon_size_big)
            } else {
                res.getDimension(R.dimen.pet_icon_size_small)
            }

            petIcon.layoutParams.width = size.toInt()
            petIcon.layoutParams.height = size.toInt()
            petIcon.load(pet.info.photo) {
                createLoadRequest(owner,  true, R.drawable.ic_baseline_pets)
            }

            petName.text = pet.info.name
            petName.show = isBig

            space1.show = isBig
            space2.show = isBig
        }
    }
}