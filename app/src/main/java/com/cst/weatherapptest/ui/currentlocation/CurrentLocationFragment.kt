package com.cst.weatherapptest.ui.currentlocation

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
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.models.forecast.WeatherListElementEntity
import com.cst.weatherapptest.R
import com.cst.weatherapptest.databinding.FragmentCurrentLocationBinding
import com.cst.weatherapptest.ui.base.BaseFragment
import com.cst.weatherapptest.ui.locationdetails.LocationWeatherForecastAdapter
import com.cst.weatherapptest.ui.locationsearch.WeatherDetailsAdapter
import com.cst.weatherapptest.util.DateUtils
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.orElse
import com.cst.weatherapptest.util.storage.PreferenceHelper.SELECTED_UNIT_OF_MEASUREMENT
import com.cst.weatherapptest.util.storage.PreferenceHelper.get
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class CurrentLocationFragment :
    BaseFragment<FragmentCurrentLocationBinding, CurrentLocationViewModel>(
        R.layout.fragment_current_location
    ) {

    companion object {
        private const val FIRST_ITEM_POSITION = 0

        private const val REQUEST_PERMISSION_LOCATION = 10
        private const val INTERVAL: Long = 2000
        private const val FASTEST_INTERVAL: Long = 1000
    }

    override fun showLoader() {
        binding.progressBar.root.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        binding.progressBar.root.visibility = View.GONE
    }

    override fun viewModelClass(): KClass<CurrentLocationViewModel> =
        CurrentLocationViewModel::class

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCurrentLocationBinding =
        FragmentCurrentLocationBinding.inflate(inflater, container, false)

    private val currentLocationViewModel: CurrentLocationViewModel by viewModels()

    private var locationWeatherForecastAdapter: LocationWeatherForecastAdapter? = null
    private val weatherDetailsAdapter = WeatherDetailsAdapter()

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var selectedUnitOfMeasurement: UnitOfMeasurement

    @Inject
    lateinit var preferenceHelper: SharedPreferences

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

    private fun initView() {
        initUnitOfMeasurement()
        initForecastedWeatherList()
        initWeatherDetailsList()

        binding.chipGroup.check(R.id.chip_today)

        getForecastForCurrentLocation()
    }

    private fun observeData() {
        observeLoader()
        observeFavoriteLocationOperations()

        currentLocationViewModel.noForecastDataCachedForThisLocation
            .observe(viewLifecycleOwner, { noForecastDataCachedForThisLocation ->
                when (noForecastDataCachedForThisLocation) {
                    true -> {
                        binding.currentLocationMainGroup.visibility = View.GONE
                        binding.noDataCachedPlaceholder.noCachedDataLayout.visibility = View.VISIBLE
                    }
                }
            })

        currentLocationViewModel.addressesByCoordinates
            .observe(viewLifecycleOwner, { addresses ->
                addresses?.firstOrNull()?.locality?.let { locality ->
                    stopLocationUpdates(locationCallback)
                    currentLocationViewModel.getLocationWeatherByName(locality)
                }.orElse {
                    Log.d(
                        "DEBUG_",
                        "locationSearchViewModel.addresses.value?.firstOrNull()?.locality == null"
                    )
                }
            })

        currentLocationViewModel.locationData.observe(
            viewLifecycleOwner,
            { locationWithWeatherEntity ->

                locationWithWeatherEntity?.let {
                    currentLocationViewModel.locationWithWeatherEntity = it
                }

                locationWithWeatherEntity.id?.let { locationId ->
                    currentLocationViewModel.locationId = locationId
                    currentLocationViewModel.getLocationWeatherForecastByLocationId(locationId)
                }
            })

        currentLocationViewModel.forecastData
            .observe(viewLifecycleOwner, { fiveDayForecastEntity ->
                currentLocationViewModel.fiveDayForecastEntity = fiveDayForecastEntity
                checkIfLocationIsInFavorites()

                showMainViewGroupItems()

                setListsByCategory(fiveDayForecastEntity)

                setLocationName(fiveDayForecastEntity)
                setSunriseTime(fiveDayForecastEntity)
                setSunsetTime(fiveDayForecastEntity)

                setWeatherForToday()

                // Save the current location id to local database
                saveCurrentLocation(fiveDayForecastEntity)
            })

        currentLocationViewModel.updatedForecastData
            .observe(viewLifecycleOwner, { fiveDayForecastEntity ->
                currentLocationViewModel.fiveDayForecastEntity = fiveDayForecastEntity
                checkIfLocationIsInFavorites()

                resetListSelectionAfterDataRefresh()

                setListsByCategory(fiveDayForecastEntity)
                setLocationName(fiveDayForecastEntity)
                setSunriseTime(fiveDayForecastEntity)
                setSunsetTime(fiveDayForecastEntity)

                when (binding.chipGroup.checkedChipId) {
                    R.id.chip_today -> {
                        setWeatherForToday()
                    }
                    R.id.chip_tomorrow -> {
                        setWeatherForTomorrow()
                    }
                    R.id.chip_next_five_days -> {
                        setWeatherForNextFiveDays()
                    }
                    else -> {
                        binding.chipGroup.check(R.id.chip_today)
                        setWeatherForToday()
                    }
                }

                showToastMessage(getString(R.string.toast_current_location_weather_data_updated))
                showMainViewGroupItems()
            })
    }

    private fun observeLoader() {
        currentLocationViewModel.isLoading
            .observe(viewLifecycleOwner, { isLoading ->
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

    private fun observeFavoriteLocationOperations() {
        currentLocationViewModel.locationIsInFavorites
            .observe(viewLifecycleOwner, { locationIsInFavorites ->
                setFavoriteIconTint(shouldShowYellowStar = locationIsInFavorites)
            })

        currentLocationViewModel.locationAddedToFavorites
            .observe(viewLifecycleOwner, { locationAddedToFavorites ->
                when (locationAddedToFavorites) {
                    true -> {
                        setFavoriteIconTint(shouldShowYellowStar = true)
                        showToastMessage(getString(R.string.toast_location_added_to_favorites))
                        // Reset the flag
                        currentLocationViewModel.locationAddedToFavorites.value = false
                    }
                }
            })

        currentLocationViewModel.locationRemovedFromFavorites
            .observe(viewLifecycleOwner, { locationRemovedFromFavorites ->
                when (locationRemovedFromFavorites) {
                    true -> {
                        setFavoriteIconTint(shouldShowYellowStar = false)
                        showToastMessage(getString(R.string.toast_location_removed_from_favorites))
                        // Reset the flag
                        currentLocationViewModel.locationRemovedFromFavorites.value = false
                    }
                }
            })
    }

    private fun setClickListeners() {
        binding.favoriteIcon.setOnClickListener {
            currentLocationViewModel.locationId?.let { locationId ->
                when (currentLocationViewModel.locationIsInFavorites.value) {
                    true -> {
                        // Location is already in favorites, we need to delete it
                        currentLocationViewModel.removeLocationWeatherForecastFromFavorites(
                            locationId
                        )
                    }
                    else -> {
                        // Location is not in favorites yet, get and store the locationWithWeatherEntity,
                        // and the forecasted data for it locally
                        currentLocationViewModel.fiveDayForecastEntity?.let { fiveDayForecastEntity ->
                            currentLocationViewModel.getLocationWithWeatherAndSaveLocal(
                                locationId, fiveDayForecastEntity
                            )
                        }.orElse {
                            showErrorDialog(getString(R.string.no_forecast_data_for_location))
                        }
                    }
                }

            }
        }

        binding.initialPermissionRequestPlaceholder.currentLocationNoDataCachedRetryButton.setOnClickListener {
            binding.initialPermissionRequestPlaceholder.permissionRequiredLayout.visibility =
                View.GONE
            getForecastForCurrentLocation()
        }

        binding.noDataCachedPlaceholder.locationDetailsNoDataCachedRetryButton.setOnClickListener {
            if (currentLocationViewModel.isDeviceConnectedToNetwork(requireContext())) {
                binding.noDataCachedPlaceholder.noCachedDataLayout.visibility = View.GONE
                getForecastForCurrentLocation()
            } else {
                showToastMessage(getString(R.string.error_still_not_connected))
            }
        }

        binding.refreshIcon.setOnClickListener {
            currentLocationViewModel.locationId?.let { locationId ->
                if (currentLocationViewModel.isDeviceConnectedToNetwork(requireContext())) {
                    currentLocationViewModel.updateLocationWeatherForecastByLocationId(
                        locationId
                    )
                } else {
                    showToastMessage(getString(R.string.error_no_internet_message))
                }
            }.orElse {
                showGeneralErrorToastMessage()
            }
        }
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_today -> {
                    locationWeatherForecastAdapter?.isShowingDays = false
                    val selectedWeatherListElementEntityFromList =
                        currentLocationViewModel.todayForecastWeatherList?.firstOrNull()
                    selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
                        showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
                    }
                    locationWeatherForecastAdapter?.setDataSet(currentLocationViewModel.todayForecastWeatherList as MutableList<WeatherListElementEntity>)
                    selectFirstForecastItem()
                }
                R.id.chip_tomorrow -> {
                    locationWeatherForecastAdapter?.isShowingDays = false
                    val selectedWeatherListElementEntityFromList =
                        currentLocationViewModel.tomorrowForecastWeatherList?.firstOrNull()
                    selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
                        showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
                    }
                    locationWeatherForecastAdapter?.setDataSet(currentLocationViewModel.tomorrowForecastWeatherList as MutableList<WeatherListElementEntity>)
                    selectFirstForecastItem()
                }
                R.id.chip_next_five_days -> {
                    locationWeatherForecastAdapter?.isShowingDays = true
                    setWeatherForNextFiveDays()
                    locationWeatherForecastAdapter?.setDataSet(currentLocationViewModel.forecastDailyList as MutableList<WeatherListElementEntity>)
                    selectFirstForecastItem()
                }
            }
        }

        locationWeatherForecastAdapter?.setOnItemClickedListener { selectedWeatherListElementEntity ->
            selectedWeatherListElementEntity?.let {
                showDetailsForSelectedWeatherListItem(it)
            }
        }
    }

    private fun initUnitOfMeasurement() {
        preferenceHelper[SELECTED_UNIT_OF_MEASUREMENT, UnitOfMeasurement.DEFAULT_UNIT_OF_MEASUREMENT_ID]?.let {
            selectedUnitOfMeasurement = Utils.getUnitOfMeasurementById(it)
        }
    }

    private fun initForecastedWeatherList() {
        locationWeatherForecastAdapter =
            LocationWeatherForecastAdapter(requireContext(), selectedUnitOfMeasurement)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.forecastWeather.layoutManager = layoutManager
        binding.forecastWeather.adapter = locationWeatherForecastAdapter
    }

    private fun initWeatherDetailsList() {
        binding.weatherAttributesList.layoutManager = LinearLayoutManager(requireContext())
        binding.weatherAttributesList.adapter = weatherDetailsAdapter
    }

    private fun getForecastForCurrentLocation() {
        if (checkPermissionForLocation(this.requireActivity())) {
            if (currentLocationViewModel.isDeviceConnectedToNetwork(requireContext())) {
                binding.initialPermissionRequestPlaceholder.permissionRequiredLayout.visibility =
                    View.GONE
                showLoader()
                startLocationUpdates(
                    this.requireContext(),
                    locationCallback
                )
            } else {
                // We show the last cached current location
                currentLocationViewModel.getCurrentLocationEntityFromLocal()
            }
        } else {
            // Show enable permission placeholder with button
            binding.currentLocationMainGroup.visibility = View.GONE
            binding.initialPermissionRequestPlaceholder.permissionRequiredLayout.visibility =
                View.VISIBLE
        }
    }

    private fun checkIfLocationIsInFavorites() {
        currentLocationViewModel.locationId?.let { locationId ->
            currentLocationViewModel.getIfLocationInFavorites(locationId)
        }
    }

    private fun resetListSelectionAfterDataRefresh() {
        locationWeatherForecastAdapter?.selectedPos = FIRST_ITEM_POSITION
        locationWeatherForecastAdapter?.notifyDataSetChanged()
        binding.forecastWeather.smoothScrollToPosition(FIRST_ITEM_POSITION)
    }

    private fun setWeatherForTomorrow() {
        currentLocationViewModel.tomorrowForecastWeatherList?.let {
            setHourlyForecastForSelectedTimePeriod(
                it
            )
        }
        val selectedWeatherListElementEntityFromList =
            currentLocationViewModel.tomorrowForecastWeatherList?.firstOrNull()
        selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
            showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
        }
    }

    private fun setWeatherForToday() {
        currentLocationViewModel.todayForecastWeatherList?.let {
            setHourlyForecastForSelectedTimePeriod(
                it
            )
        }
        val selectedWeatherListElementEntityFromList =
            currentLocationViewModel.todayForecastWeatherList?.firstOrNull()
        selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
            showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
        }

    }

    private fun setWeatherForNextFiveDays() {
        val selectedWeatherListElementEntityFromList =
            currentLocationViewModel.forecastDailyList?.firstOrNull()
        selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
            showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
        }
    }

    private fun selectFirstForecastItem() {
        locationWeatherForecastAdapter?.resetListSelection()
        binding.forecastWeather.scrollToPosition(FIRST_ITEM_POSITION)
    }

    private fun showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity: WeatherListElementEntity) {
        setWeatherIcon(selectedWeatherListElementEntity)
        setWeatherTemperature(selectedWeatherListElementEntity)
        setMinMaxTemperature(selectedWeatherListElementEntity)
        setWeatherDescription(selectedWeatherListElementEntity)
        setDetailsForSelectedForecastItem(selectedWeatherListElementEntity)
    }

    private fun setListsByCategory(fiveDayForecastEntity: FiveDayForecastEntity) {
        currentLocationViewModel.todayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate -> DateUtils.isToday(predicate.dt) }
        currentLocationViewModel.tomorrowForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate -> DateUtils.isTomorrow(predicate.dt) }
        currentLocationViewModel.tomorrowPlus1DayForecastList =
            fiveDayForecastEntity.list?.filter { predicate -> DateUtils.isTomorrowPlus1Day(predicate.dt) }
        currentLocationViewModel.tomorrowPlus2DayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate ->
                DateUtils.isTomorrowPlus2Days(
                    predicate.dt
                )
            }
        currentLocationViewModel.tomorrowPlus3DayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate ->
                DateUtils.isTomorrowPlus3Days(
                    predicate.dt
                )
            }
        currentLocationViewModel.tomorrowPlus4DayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate ->
                DateUtils.isTomorrowPlus4Days(
                    predicate.dt
                )
            }

        // We remove today from the list because we want to see the next five days
        val cleanedListForecastDailyList =
            currentLocationViewModel.createForecastedWeatherListByDays() as MutableList<WeatherListElementEntity>
        cleanedListForecastDailyList.removeFirst()
        currentLocationViewModel.forecastDailyList = cleanedListForecastDailyList
    }

    private fun setDetailsForSelectedForecastItem(selectedWeatherListElementEntity: WeatherListElementEntity) {
        weatherDetailsAdapter.setDataSet(
            currentLocationViewModel.createWeatherAttributeList(
                requireContext(),
                selectedWeatherListElementEntity,
                selectedUnitOfMeasurement
            )
        )
    }

    private fun setHourlyForecastForSelectedTimePeriod(list: List<WeatherListElementEntity>) {
        locationWeatherForecastAdapter?.setDataSet(list as MutableList<WeatherListElementEntity>)
    }

    private fun setWeatherDescription(selectedWeatherListElementEntity: WeatherListElementEntity) {
        selectedWeatherListElementEntity.weather?.firstOrNull()?.description?.let { description ->
            binding.forecastDescription.text = description
        }
    }

    private fun setMinMaxTemperature(selectedWeatherListElementEntity: WeatherListElementEntity) {
        val tempMax = selectedWeatherListElementEntity.main?.tempMax
        val tempMin = selectedWeatherListElementEntity.main?.tempMin
        if (tempMax != null && tempMin != null) {
            val tempMaxFormatted =
                Utils.getTemperatureWithUnit(tempMax, selectedUnitOfMeasurement)
            val tempMinFormatted =
                Utils.getTemperatureWithUnit(tempMin, selectedUnitOfMeasurement)
            val maxMinTemperature = "$tempMaxFormatted / $tempMinFormatted"
            binding.forecastMinMaxTemperature.text = maxMinTemperature
        }
    }

    private fun setWeatherTemperature(selectedWeatherListElementEntity: WeatherListElementEntity) {
        selectedWeatherListElementEntity.main?.temp?.let { temp ->
            binding.forecastTemperature.text =
                Utils.getTemperatureWithUnit(temp, selectedUnitOfMeasurement)
        }
    }

    private fun setWeatherIcon(selectedWeatherListElementEntity: WeatherListElementEntity) {
        selectedWeatherListElementEntity.weather?.firstOrNull()?.icon?.let { iconIdentifier ->
            Glide.with(requireContext())
                .load(Utils.getWeatherTypeIconByIdentifier(requireContext(), iconIdentifier))
                .into(binding.forecastIcon)
        }
    }

    private fun setSunsetTime(fiveDayForecastEntity: FiveDayForecastEntity) {
        fiveDayForecastEntity.city?.sunset?.let {
            binding.sunsetText.text = DateUtils.convertLongToHour(it)
        }
    }

    private fun setSunriseTime(fiveDayForecastEntity: FiveDayForecastEntity) {
        fiveDayForecastEntity.city?.sunrise?.let {
            binding.sunriseText.text = DateUtils.convertLongToHour(it)
        }
    }

    private fun setLocationName(fiveDayForecastEntity: FiveDayForecastEntity) {
        val locationName =
            "${fiveDayForecastEntity.city?.name}, ${fiveDayForecastEntity.city?.country}"
        binding.locationName.text = locationName
    }

    private fun setFavoriteIconTint(shouldShowYellowStar: Boolean) {
        binding.favoriteIcon.setImageResource(if (shouldShowYellowStar) R.drawable.ic_round_star_24 else R.drawable.ic_round_star_24_grey)
    }

    private fun checkPermissionForLocation(activity: Activity): Boolean {
        return if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
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
            currentLocationViewModel.getLocationAddressesFromGeocoderByCoordinates(
                locationResult.lastLocation.latitude,
                locationResult.lastLocation.longitude,
            )
        }
    }

    private fun saveCurrentLocation(fiveDayForecastEntity: FiveDayForecastEntity) {
        fiveDayForecastEntity.city?.id?.let { locationId ->
            currentLocationViewModel.saveCurrentLocationEntity(locationId)
        }
    }

    private fun showMainViewGroupItems() {
        binding.currentLocationMainGroup.visibility = View.VISIBLE
    }
}