package com.cst.weatherapptest.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cst.weatherapptest.ui.currentlocation.CurrentLocationFragment
import com.cst.weatherapptest.ui.locations.LocationsFragment

class WeatherPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    companion object {
        const val LOCATIONS_PAGE_INDEX = 0
        const val CURRENT_LOCATION_PAGE_INDEX = 1
    }

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        LOCATIONS_PAGE_INDEX to { LocationsFragment() },
        CURRENT_LOCATION_PAGE_INDEX to { CurrentLocationFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
