package com.buddies.gallery.ui.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.buddies.gallery.R

class GalleryListDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        val size = view.context.resources.getDimensionPixelSize(R.dimen.gallery_decoration_size)

        outRect.top = if (position == 0 || position == 1) size else 0
        outRect.bottom = size
        outRect.left = if (position % 2 == 0) size else 0
        outRect.right = size
    }
}