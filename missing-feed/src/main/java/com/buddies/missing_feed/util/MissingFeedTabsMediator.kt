package com.buddies.missing_feed.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.viewpager2.widget.ViewPager2
import com.buddies.missing_feed.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MissingFeedTabsMediator(
    context: Context,
    private val tabLayout: TabLayout,
    private val viewPager2: ViewPager2
) {

    private val strategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
        tab.text = context.resources.getString(TAB.byPosition(position).title)
    }

    fun connect() = TabLayoutMediator(tabLayout, viewPager2, strategy).attach()

    internal enum class TAB(
        val position: Int,
        @StringRes val title: Int
    ) {
        LOST_TAB(0, R.string.lost_tab_title),
        FOUND_TAB(1, R.string.found_tab_title);

        companion object {
            fun byPosition(position: Int) = when {
                position == LOST_TAB.position -> LOST_TAB
                position == FOUND_TAB.position -> FOUND_TAB
                else -> throw IllegalArgumentException("Illegal position $position, out of bounds (max: 1).")
            }
        }
    }
}