package com.buddies.missing_feed.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.missing_feed.ui.fragment.MissingFeedFragment
import com.buddies.missing_feed.ui.fragment.MissingFeedTabFragment
import com.buddies.missing_feed.util.MissingFeedTabsMediator.TAB
import com.buddies.missing_feed.util.MissingFeedTabsMediator.TAB.FOUND_TAB
import com.buddies.missing_feed.util.MissingFeedTabsMediator.TAB.LOST_TAB
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MissingFeedTabsAdapter(
    fragment: MissingFeedFragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = TAB.values().size

    override fun createFragment(position: Int): Fragment = when {
        position == LOST_TAB.position -> MissingFeedTabFragment.newInstance(LOST.name)
        position == FOUND_TAB.position -> MissingFeedTabFragment.newInstance(FOUND.name)
        else -> throw IllegalArgumentException("illegal position $position, out of bounds (max: $itemCount).")
    }
}