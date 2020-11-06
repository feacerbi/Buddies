package com.buddies.mypets.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class GalleryAdapter(
    fragment: Fragment,
    private val list: List<Uri>
) : FragmentStateAdapter(fragment.parentFragmentManager, fragment.lifecycle) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment =
        GalleryFragment().apply {
            arguments = Bundle().apply {
                putString(GalleryFragment.URI_EXTRA, list[position].toString())
            }
        }
}