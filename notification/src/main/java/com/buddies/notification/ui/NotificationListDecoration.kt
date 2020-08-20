package com.buddies.notification.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class NotificationListDecoration(
    context: Context
) : DividerItemDecoration(context, VERTICAL) {

    private val divider: Drawable?

    init {
        with (context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))) {
            divider = getDrawable(0).also { it?.alpha = DIVIDER_ALPHA }
            recycle()
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (divider == null) return

        canvas.save()

        val left = 0
        val right = parent.width
        val bounds = Rect()

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom: Int = bounds.bottom + child.translationY.roundToInt()
            val top: Int = bottom - divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }

    companion object {
        private const val DIVIDER_ALPHA = 177
    }
}