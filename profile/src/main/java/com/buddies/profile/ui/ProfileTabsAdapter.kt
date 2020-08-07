package com.buddies.profile.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileTabsAdapter(
    fragment: ProfileFragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> ProfileInfoFragment()
            1 -> ProfileNotificationsFragment()
            else -> throw IllegalArgumentException("illegal position $position, out of bounds (max: $itemCount).")
        }
}