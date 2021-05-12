package com.cst.weatherapptest.ui.locations

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.weatherapptest.R
import com.cst.weatherapptest.databinding.FragmentLocationsBinding
import com.cst.weatherapptest.ui.base.BaseFragment
import com.cst.weatherapptest.ui.main.MainViewPagerFragmentDirections
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.hideKeyboard
import com.cst.weatherapptest.util.setSearchInputListener
import com.cst.weatherapptest.util.storage.PreferenceHelper
import com.cst.weatherapptest.util.storage.PreferenceHelper.get
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class LocationsFragment : BaseFragment<FragmentLocationsBinding, LocationsViewModel>(
    R.layout.fragment_locations
) {

    companion object {
        private const val FIRST_POSITION = 0
        private const val EMPTY_STRING = ""
    }

    private var favoriteLocationsListAdapter: FavoriteLocationsListAdapter? = null

    private val locationsViewModel: LocationsViewModel by viewModels()

    private var searchTerm: String = EMPTY_STRING

    private lateinit var selectedUnitOfMeasurement: UnitOfMeasurement

    @Inject
    lateinit var preferenceHelper: SharedPreferences

    override fun showLoader() {
        binding.progressBar.root.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        binding.progressBar.root.visibility = View.GONE
    }

    override fun viewModelClass(): KClass<LocationsViewModel> = LocationsViewModel::class

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLocationsBinding =
        FragmentLocationsBinding.inflate(inflater, container, false)

    override fun onBackPressed() {
        super.onBackPressed()
        requireActivity().finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        resetSearchField()
        locationsViewModel.getWeatherForLocationGroup(requireContext())
    }

    private fun initView() {
        initUnitOfMeasurement()
        initFavoriteLocationsAdapter()
        initSearchListener()
        if (locationsViewModel.isDeviceConnectedToNetwork(requireContext()).not()) {
            showOfflineSnackBar()
        }
    }

    private fun observeData() {
        locationsViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            when (isLoading) {
                true -> {
                    showLoader()
                }
                false -> {
                    hideLoader()
                }
            }
        })

        locationsViewModel.showEmptyListPlaceholder.observe(
            viewLifecycleOwner,
            { showEmptyListPlaceholder ->
                when (showEmptyListPlaceholder) {
                    true -> {
                        showEmptyListPlaceholder()
                    }
                    false -> {
                        hideEmptyListPlaceholder()
                    }
                }
            })

        locationsViewModel.locationGroupData.observe(
            viewLifecycleOwner,
            { locationGroupData ->
                locationGroupData?.let {
                    if (it.isEmpty()) {
                        showEmptyListPlaceholder()
                    } else {
                        hideEmptyListPlaceholder()
                        locationsViewModel.allLocationsWithWeather =
                            it as MutableList<LocationWithWeatherEntity>
                        favoriteLocationsListAdapter?.setDataSet(it)
                        binding.locationsList.visibility = View.VISIBLE

                        showDataUpdatedToastIfNecessary()
                    }
                }
            })
    }

    private fun showDataUpdatedToastIfNecessary() {
        if (locationsViewModel.isLocationsDataRefreshing.value == true) {
            showToastMessage(getString(R.string.toast_locations_weather_data_updated))
            locationsViewModel.isLocationsDataRefreshing.value = false
        }
    }

    private fun setClickListeners() {
        binding.locationsRefreshButton.setOnClickListener {
            if (locationsViewModel.isDeviceConnectedToNetwork(requireContext())) {
                locationsViewModel.isLocationsDataRefreshing.value = true
                resetSearchField()
                locationsViewModel.getWeatherForLocationGroup(requireContext())
            } else {
                showConnectionRequiredToast()
            }
        }

        favoriteLocationsListAdapter?.setOnItemClickedListener { locationWithWeatherEntity ->
            locationWithWeatherEntity?.id?.let { locationId ->
                hideKeyboard()
                val direction =
                    MainViewPagerFragmentDirections.actionMainViewPagerFragmentToLocationDetailsFragment(
                        locationId
                    )
                navController.navigate(direction)
            }
        }
    }

    private fun initUnitOfMeasurement() {
        preferenceHelper[PreferenceHelper.SELECTED_UNIT_OF_MEASUREMENT, UnitOfMeasurement.DEFAULT_UNIT_OF_MEASUREMENT_ID]?.let {
            selectedUnitOfMeasurement = Utils.getUnitOfMeasurementById(it)
        }
    }

    private fun initFavoriteLocationsAdapter() {
        favoriteLocationsListAdapter =
            FavoriteLocationsListAdapter(requireContext(), selectedUnitOfMeasurement)
        binding.locationsList.layoutManager = LinearLayoutManager(requireContext())
        binding.locationsList.adapter = favoriteLocationsListAdapter
    }

    private fun initSearchListener() {
        binding.searchView.setSearchInputListener {
            searchTermEntered {
                searchTerm = binding.searchView.text.toString()
                if (searchTerm.isEmpty().not()) {
                    favoriteLocationsListAdapter?.filter?.filter(searchTerm)
                }
            }
            searchTermCleared {
                searchTerm = EMPTY_STRING
                // Show the full list again if user clears the search field
                locationsViewModel.allLocationsWithWeather?.let { allSavedLocationsWithWeather ->
                    favoriteLocationsListAdapter?.setDataSet(allSavedLocationsWithWeather)
                }
                binding.locationsList.smoothScrollToPosition(FIRST_POSITION)
            }
        }
    }

    private fun showOfflineSnackBar() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            getString(R.string.snackbar_app_in_offline_mode),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showEmptyListPlaceholder() {
        binding.locationsEmptyTextPlaceholder.visibility = View.VISIBLE
        binding.locationsList.visibility = View.GONE
    }

    private fun hideEmptyListPlaceholder() {
        binding.locationsEmptyTextPlaceholder.visibility = View.GONE
    }

    private fun showConnectionRequiredToast() {
        showToastMessage(getString(R.string.error_no_internet_message))
    }

    private fun resetSearchField() {
        binding.searchView.text.clear()
        searchTerm = EMPTY_STRING
    }
}
