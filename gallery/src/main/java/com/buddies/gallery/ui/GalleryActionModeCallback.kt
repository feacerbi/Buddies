package com.buddies.gallery.ui

import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.buddies.gallery.R

class GalleryActionModeCallback(
    private val deleteAction: (ActionMode) -> Unit = {},
    private val destroyAction: () -> Unit = {}
) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.gallery_toolbar_action_mode_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_gallery_menu_action -> {
                deleteAction.invoke(mode)
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        destroyAction.invoke()
    }
}