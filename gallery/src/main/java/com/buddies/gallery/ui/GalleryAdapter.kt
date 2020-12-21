package com.buddies.gallery.ui

import android.net.Uri
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.gallery.databinding.GalleryItemBinding

class GalleryAdapter(
    private val owner: LifecycleOwner
) : ListAdapter<Uri, GalleryAdapter.GalleryViewHolder>(GalleryDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder =
        GalleryViewHolder(
            GalleryItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GalleryViewHolder(
        private val binding: GalleryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Uri) {
            binding.picture.load(item, owner)
        }
    }

    private class GalleryDiffUtil : DiffUtil.ItemCallback<Uri>() {

        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean =
            oldItem == newItem
    }
}