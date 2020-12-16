package com.buddies.pet.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class GalleryAdapter(
    fragment: Fragment,
    items: List<Uri> = mutableListOf()
) : FragmentStateAdapter(fragment) {

    private val list: MutableList<Uri> = mutableListOf()

    init {
        updateItems(items)
    }

    fun updateItems(items: List<Uri>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment =
        GalleryFragment().apply {
            arguments = Bundle().apply {
                putString(GalleryFragment.URI_EXTRA, list[position].toString())
            }
        }
}