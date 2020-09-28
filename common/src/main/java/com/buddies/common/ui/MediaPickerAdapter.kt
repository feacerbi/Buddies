package com.buddies.common.ui

import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.R
import com.buddies.common.databinding.MediaListItemBinding
import com.buddies.common.ui.MediaPickerAdapter.MediaViewHolder
import com.buddies.common.util.inflater

class MediaPickerAdapter(
    private val onClick: ((MediaSource) -> Unit)?
) : RecyclerView.Adapter<MediaViewHolder>() {

    private val mediaList = listOf(
        MediaSource.GALLERY,
        MediaSource.CAMERA
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder =
        MediaViewHolder(
            MediaListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = mediaList.size

    inner class MediaViewHolder(
        val binding: MediaListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
           val mediaSource = mediaList[position]

            mediaIcon.setImageResource(mediaSource.icon)
            mediaDescription.text = root.context.getString(mediaSource.description)

            root.setOnClickListener {
                onClick?.invoke(mediaSource)
            }
        }

    }

    enum class MediaSource(
        @DrawableRes val icon: Int,
        @StringRes val description: Int
    ) {
        GALLERY(R.drawable.ic_baseline_collections, R.string.gallery_source_description),
        CAMERA(R.drawable.ic_baseline_camera_alt, R.string.camera_source_description)
    }
}