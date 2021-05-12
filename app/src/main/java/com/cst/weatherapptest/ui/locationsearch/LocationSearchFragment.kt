package com.cst.weatherapptest.ui.locationsearch

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.weatherapptest.R
import com.cst.weatherapptest.databinding.FragmentLocationSearchBinding
import com.cst.weatherapptest.ui.base.BaseFragment
import com.cst.weatherapptest.util.*
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.storage.PreferenceHelper.SELECTED_UNIT_OF_MEASUREMENT
import com.cst.weatherapptest.util.storage.PreferenceHelper.get
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * In this class the user can search for specific location using Geocoder
 * The user also can search for his/her current location with the Fused Location Provider API,
 * which gives back the user's location in latitude and longitude.
 *
 * The fused location provider is a location API in Google Play services that intelligently combines
 * different signals to provide the location information that your app needs.
 */
@AndroidEntryPoint
class LocationSearchFragment : BaseFragment<FragmentLocationSearchBinding, LocationSearchViewModel>(
    R.layout.fragment_location_search
) {
    companion object {
        private const val EMPTY_STRING = ""

        private const val REQUEST_PERMISSION_LOCATION = 10
        private const val INTERVAL: Long = 2000
        private const val FASTEST_INTERVAL: Long = 1000

        private const val SEARCH_TERM_MIN_LENGTH = 3
    }

    private val locationSearchViewModel: LocationSearchViewModel by viewModels()
    private val locationSearchAdapter = LocationSearchAdapter()
    private val weatherDetailsAdapter = WeatherDetailsAdapter()

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var selectedUnitOfMeasurement: UnitOfMeasurement

    private var searchTerm: String = EMPTY_STRING

    @Inject
    lateinit var preferenceHelper: SharedPreferences

    override fun viewModelClass(): KClass<LocationSearchViewModel> = LocationSearchViewModel::class

    override fun showLoader() {
        binding.progressBar.root.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        binding.progressBar.root.visibility = View.GONE
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLocationSearchBinding =
        FragmentLocationSearchBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
        setClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates(locationCallback)
    }

    private fun initView() {
        initUnitOfMeasurement()
        if (locationSearchViewModel.isDeviceConnectedToNetwork(requireContext())) {
            initLocationSearchAdapter()
            initWeatherDetailsAdapter()

            binding.locationSearchTextInput.setSearchInputListener {
                searchTermEntered {
                    searchTerm = binding.locationSearchTextInput.text.toString()
                    if (searchTerm.length > SEARCH_TERM_MIN_LENGTH) {
                        searchTermChanged(searchTerm)
                    }
                }
                searchTermCleared {
                    showInitialSearchPlaceholder()
                }
            }

            binding.emptyListPlaceholder.emptyConstraint.visibility = View.VISIBLE
        } else {
            showDeviceNotConnectedPlaceholder()
        }
    }

    private fun observeData() {
        observeLoader()

        locationSearchViewModel.searchErrorAppeared.observe(
            viewLifecycleOwner,
            { searchErrorAppeared ->
                when (searchErrorAppeared) {
                    true -> {
                        showErrorDialog(getString(R.string.error_geocoder_connectivity_exception_message))
                        locationSearchViewModel.searchErrorAppeared.value = false
                    }
                }
            })

        locationSearchViewModel.addressesByName
            .observe(viewLifecycleOwner, { addresses ->
                locationSearchViewModel.locationList.clear()
                if (addresses.isNullOrEmpty().not()) {
                    binding.emptyListPlaceholder.root.visibility = View.GONE
                    addresses.forEach { address ->
                        if (address.locality != null) {
                            locationSearchViewModel.locationList.add(address)
                        }
                    }
                    if (locationSearchViewModel.locationList.isNotEmpty()) {
                        locationSearchAdapter.setDataSet(locationSearchViewModel.locationList)
                        binding.locationSearchResultList.visibility = View.VISIBLE
                    } else {
                        showNoLocationFoundPlaceholder()
                    }
                } else {
                    showNoLocationFoundPlaceholder()
                }

            })

        locationSearchViewModel.addressesByCoordinates
            .observe(viewLifecycleOwner, { addresses ->
                addresses?.firstOrNull()?.locality?.let { locality ->
                    hideLoader()
                    stopLocationUpdates(locationCallback)
                    locationSearchViewModel.getLocationWeatherByName(locality)
                }.orElse {
                    Log.d(
                        "DEBUG",
                        "locationSearchViewModel.addresses.value?.firstOrNull()?.locality == null"
                    )
                }
            })

        locationSearchViewModel.locationData.observe(
            viewLifecycleOwner,
            { locationWithWeatherEntity ->
                locationSearchViewModel.locationWithWeatherEntity = locationWithWeatherEntity
                initLocationCard(locationWithWeatherEntity)
            })

        locationSearchViewModel.locationAddedToFavorites.observe(
            viewLifecycleOwner,
            { addedToFavorites ->
                when (addedToFavorites) {
                    true -> {
                        showToastMessage(getString(R.string.toast_location_added_to_favorites))
                    }
                }
            })
    }

    private fun observeLoader() {
        locationSearchViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            when (isLoading) {
                true -> {
                    showLoader()
                }
                false -> {
                    hideLoader()
                }
            }
        })
    }

    private fun setClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.notConnectedToNetworkPlaceholder.locationSearchNotConnectedRetryButton.setOnClickListener {
            if (locationSearchViewModel.isDeviceConnectedToNetwork(requireContext())) {
                hideDeviceNotConnectedPlaceholder()
                initView()
            } else {
                showToastMessage(getString(R.string.error_still_not_connected))
            }
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.provide_location -> {
                    resetSearchField()
                    hideKeyboard()
                    if (checkPermissionForLocation(this.requireActivity())) {
                        if (locationSearchViewModel.isDeviceConnectedToNetwork(requireContext())) {
                            showLoader()
                            startLocationUpdates(
                                this.requireContext(),
                                locationCallback
                            )
                        } else {
                            showToastMessage(getString(R.string.error_no_internet_message))
                        }
                    }
                }
            }
            true
        }

        locationSearchAdapter.setOnItemClickedListener { address ->
            address?.let {
                it.locality?.let { locality ->
                    hideKeyboard()
                    locationSearchViewModel.getLocationWeatherByName(locality)
                }.orElse {
                    showErrorDialog(getString(R.string.error_locality_not_found))
                }
            }.orElse {
                showErrorDialog(getString(R.string.error_address_is_not_correct))
            }
        }
        binding.buttonAddToFavorites.setOnClickListener {
            saveLocationWithWeather()
            locationSearchViewModel.locationWithWeatherEntity?.id?.let { locationId ->
                locationSearchViewModel.getAndSaveForecastForLocationById(locationId)
            }
        }
        binding.buttonShowDetailedWeather.setOnClickListener {
            locationSearchViewModel.locationWithWeatherEntity?.id?.let { id ->
                val direction =
                    LocationSearchFragmentDirections.actionLocationSearchFragmentToLocationDetailsFragment(
                        id
                    )
                navController.navigate(direction)
            }
        }
    }

    private fun resetSearchField() {
        binding.locationSearchTextInput.setText(EMPTY_STRING)
        binding.locationSearchTextInput.clearFocus()
    }

    private fun showInitialSearchPlaceholder() {
        locationSearchViewModel.locationList.clear()
        locationSearchAdapter.setDataSet(locationSearchViewModel.locationList)

        binding.emptyListPlaceholder.emptyLocationListPlaceholderText.text =
            getString(R.string.empty_location_list_placeholder_text)
        binding.weatherDetailsCard.visibility = View.GONE

        binding.locationSearchResultList.visibility = View.GONE
        binding.emptyListPlaceholder.emptyConstraint.visibility = View.VISIBLE
    }

    private fun initUnitOfMeasurement() {
        preferenceHelper[SELECTED_UNIT_OF_MEASUREMENT, UnitOfMeasurement.DEFAULT_UNIT_OF_MEASUREMENT_ID]?.let {
            selectedUnitOfMeasurement = Utils.getUnitOfMeasurementById(it)
        }
    }

    private fun showDeviceNotConnectedPlaceholder() {
        binding.locationSearchTextInputLayout.visibility = View.GONE
        binding.locationSearchResultList.visibility = View.GONE
        binding.toolbar.menu.findItem(R.id.provide_location).isVisible = false
        binding.emptyListPlaceholder.liearlayout.visibility = View.GONE

        binding.notConnectedToNetworkPlaceholder.noInternetLayout.visibility = View.VISIBLE
    }

    private fun hideDeviceNotConnectedPlaceholder() {
        binding.locationSearchTextInputLayout.visibility = View.VISIBLE
        binding.locationSearchResultList.visibility = View.VISIBLE
        binding.toolbar.menu.findItem(R.id.provide_location).isVisible = true
        binding.emptyListPlaceholder.liearlayout.visibility = View.VISIBLE

        binding.notConnectedToNetworkPlaceholder.noInternetLayout.visibility = View.GONE
    }

    private fun initWeatherDetailsAdapter() {
        binding.weatherAttributesList.layoutManager = LinearLayoutManager(requireContext())
        binding.weatherAttributesList.adapter = weatherDetailsAdapter
    }

    private fun initLocationSearchAdapter() {
        binding.locationSearchResultList.layoutManager = LinearLayoutManager(requireContext())
        binding.locationSearchResultList.adapter = locationSearchAdapter
    }


    private fun showNoLocationFoundPlaceholder() {
        binding.locationSearchResultList.visibility = View.GONE
        binding.emptyListPlaceholder.emptyLocationListPlaceholderText.text =
            getString(R.string.placeholder_no_location_found)
        binding.emptyListPlaceholder.emptyConstraint.visibility = View.VISIBLE
    }

    private fun initLocationCard(locationWithWeatherEntity: LocationWithWeatherEntity) {
        binding.locationName.text = locationWithWeatherEntity.name
        binding.weatherDescription.text =
            locationWithWeatherEntity.weatherResponseData?.firstOrNull()?.description
        locationWithWeatherEntity.weatherResponseData?.firstOrNull()?.icon?.let { iconIdentifier ->
            Glide.with(requireContext())
                .load(Utils.getWeatherTypeIconByIdentifier(requireContext(), iconIdentifier))
                .into(binding.weatherIcon)
        }

        locationWithWeatherEntity.main?.temp?.let { temperature ->
            binding.weatherTemperature.setTextUnitConverted(
                temperature,
                selectedUnitOfMeasurement
            )
        }

        weatherDetailsAdapter.setDataSet(
            locationSearchViewModel.createWeatherAttributeList(
                requireContext(),
                locationWithWeatherEntity,
                selectedUnitOfMeasurement
            )
        )
        binding.locationSearchResultList.visibility = View.GONE
        binding.weatherDetailsCard.visibility = View.VISIBLE
    }

    private fun saveLocationWithWeather() {
        locationSearchViewModel.locationWithWeatherEntity?.let {
            locationSearchViewModel.saveLocationWithWeatherToFavorites(it)
        }
    }

    private fun searchTermChanged(searchTerm: String?) {
        if (searchTerm.isNullOrEmpty().not()) {
            if (locationSearchViewModel.isDeviceConnectedToNetwork(requireContext())) {
                showLoader()
                locationSearchViewModel.locationList.clear()
                locationSearchAdapter.setDataSet(locationSearchViewModel.locationList)
                locationSearchViewModel.getLocationAddressesFromGeocoderByName(searchTerm.toString())
            } else {
                showToastMessage(getString(R.string.error_no_internet_message))
            }
        } else {
            binding.locationSearchResultList.visibility = View.GONE
            locationSearchViewModel.locationList.clear()
            locationSearchAdapter.setDataSet(locationSearchViewModel.locationList)
            binding.emptyListPlaceholder.root.visibility = View.VISIBLE
        }
    }

    private fun checkPermissionForLocation(activity: Activity): Boolean {
        return if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            binding.toolbar.menu.findItem(R.id.provide_location).icon.setTint(
                resources.getColor(
                    R.color.cta1,
                    null
                )
            )
            true
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LOCATION
            )
            false
        }
    }

    private fun startLocationUpdates(context: Context, locationCallback: LocationCallback) {
        val locationRequest = LocationRequest.create().apply {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates(locationCallback: LocationCallback) {
        if (fusedLocationClient != null) {
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationSearchViewModel.getLocationAddressesFromGeocoderByCoordinates(
                locationResult.lastLocation.latitude,
                locationResult.lastLocation.longitude,
            )
        }
    }
}