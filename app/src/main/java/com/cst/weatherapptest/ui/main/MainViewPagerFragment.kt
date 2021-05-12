package com.cst.weatherapptest.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cst.weatherapptest.R
import com.cst.weatherapptest.databinding.FragmentMainViewPagerBinding
import com.cst.weatherapptest.ui.base.BaseFragment
import com.cst.weatherapptest.ui.main.WeatherPagerAdapter.Companion.CURRENT_LOCATION_PAGE_INDEX
import com.cst.weatherapptest.ui.main.WeatherPagerAdapter.Companion.LOCATIONS_PAGE_INDEX
import com.cst.weatherapptest.util.hideKeyboard
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainViewPagerFragment :
    BaseFragment<FragmentMainViewPagerBinding, MainViewPagerViewModel>(
        R.layout.fragment_main_view_pager
    ) {

    override fun viewModelClass(): KClass<MainViewPagerViewModel> = MainViewPagerViewModel::class

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainViewPagerBinding =
        FragmentMainViewPagerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.weatherTab
        val viewPager = binding.weatherViewPager

        /*
           Disable swipe on ViewPager
           because we have a horizontal list on the Current Location fragment
           and it could be confusing for the user
         */
        viewPager.isUserInputEnabled = false

        viewPager.adapter = WeatherPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    hideKeyboard()
                    val direction =
                        MainViewPagerFragmentDirections.actionMainViewPagerFragmentToLocationSearchFragment()
                    navController.navigate(direction)
                    return@setOnMenuItemClickListener true
                }
                R.id.settings -> {
                    hideKeyboard()
                    val direction =
                        MainViewPagerFragmentDirections.actionMainViewPagerFragmentToSettingsFragment()
                    navController.navigate(direction)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            LOCATIONS_PAGE_INDEX -> getString(R.string.fragment_title_locations)
            CURRENT_LOCATION_PAGE_INDEX -> getString(R.string.fragment_title_current_location)
            else -> null
        }
    }

    override fun showLoader() {
    }

    override fun hideLoader() {
    }

}
