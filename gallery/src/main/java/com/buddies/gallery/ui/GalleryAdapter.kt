package com.buddies.gallery.ui

import android.content.Context
import android.view.ActionMode
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.gallery.R
import com.buddies.gallery.databinding.GalleryItemBinding
import com.buddies.server.model.Picture

class GalleryAdapter(
    private val owner: LifecycleOwner,
    private val startActionMode: () -> ActionMode
) : ListAdapter<Picture, GalleryAdapter.GalleryViewHolder>(GalleryDiffUtil()) {

    private var actionMode: ActionMode? = null
    private var selectedPictures = mutableSetOf<Picture>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder =
        GalleryViewHolder(
            GalleryItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class GalleryViewHolder(
        private val binding: GalleryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with (binding) {
            val item = getItem(position)

            picture.load(item.downloadUri, owner)

            pictureSelectedView.isSelected = selectedPictures.contains(item)

            root.setOnLongClickListener {
                if (actionMode == null) {
                    enableActionMode(root.context, startActionMode.invoke())
                }

                togglePictureSelection(root.context, item)
                true
            }

            root.setOnClickListener {
                if (actionMode != null) {
                    togglePictureSelection(root.context, item)
                } else {
                    openFullscreenPicture(item)
                }
            }
        }
    }

    private fun togglePictureSelection(context: Context, item: Picture) {
        if (selectedPictures.contains(item)) {
            selectedPictures.remove(item)
        } else {
            selectedPictures.add(item)
        }

        updateActionModeTitle(context)
        notifyDataSetChanged()
    }

    private fun openFullscreenPicture(item: Picture) {
        // TODO Open fullscreen picture
    }

    private fun updateActionModeTitle(context: Context) {
        actionMode?.title = context.resources.getQuantityString(
            R.plurals.action_mode_title, selectedPictures.size, selectedPictures.size)
    }

    fun getSelectedPictureIds(): List<String> = selectedPictures.toList().map { it.id }

    fun enableActionMode(context: Context, newActionMode: ActionMode) {
        actionMode = newActionMode
        updateActionModeTitle(context)
    }

    fun disableActionMode() {
        actionMode?.finish()
        actionMode = null
        selectedPictures.clear()
        notifyDataSetChanged()
    }

    private class GalleryDiffUtil : DiffUtil.ItemCallback<Picture>() {

        override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Picture, newItem: Picture): Boolean =
            oldItem.downloadUri == newItem.downloadUri
    }
}