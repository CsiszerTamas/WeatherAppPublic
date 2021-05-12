package com.cst.weatherapptest.ui.locationdetails

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.models.forecast.WeatherListElementEntity
import com.cst.weatherapptest.R
import com.cst.weatherapptest.databinding.FragmentLocationDetailsBinding
import com.cst.weatherapptest.ui.base.BaseFragment
import com.cst.weatherapptest.ui.locationsearch.WeatherDetailsAdapter
import com.cst.weatherapptest.util.DateUtils
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.orElse
import com.cst.weatherapptest.util.storage.PreferenceHelper.SELECTED_UNIT_OF_MEASUREMENT
import com.cst.weatherapptest.util.storage.PreferenceHelper.get
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass


@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding, LocationDetailsViewModel>(
        R.layout.fragment_location_details
    ) {

    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    companion object {
        private const val FIRST_ITEM_POSITION = 0
    }


    override fun showLoader() {
        shimmerFrameLayout.showShimmer(true)
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.hideShimmer()
        shimmerFrameLayout.visibility = View.GONE
    }

    override fun viewModelClass(): KClass<LocationDetailsViewModel> =
        LocationDetailsViewModel::class

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLocationDetailsBinding =
        FragmentLocationDetailsBinding.inflate(inflater, container, false)

    private val locationDetailsViewModel: LocationDetailsViewModel by viewModels()

    private var locationWeatherForecastAdapter: LocationWeatherForecastAdapter? = null
    private val weatherDetailsAdapter = WeatherDetailsAdapter()

    private lateinit var selectedUnitOfMeasurement: UnitOfMeasurement

    @Inject
    lateinit var preferenceHelper: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkArguments()
        initView()
        observeData()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        shimmerFrameLayout.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        shimmerFrameLayout.stopShimmer()
    }

    private fun checkArguments() {
        val args: LocationDetailsFragmentArgs by navArgs()
        locationDetailsViewModel.locationId = args.locationId
    }

    private fun initUnitOfMeasurement() {
        preferenceHelper[SELECTED_UNIT_OF_MEASUREMENT, UnitOfMeasurement.DEFAULT_UNIT_OF_MEASUREMENT_ID]?.let {
            selectedUnitOfMeasurement = Utils.getUnitOfMeasurementById(it)
        }
    }

    private fun initView() {
        // Init shimmer loading animation layout
        shimmerFrameLayout = binding.shimmerFrameLayout

        // Hide the main and detail cards at init
        showCards(show = false)

        showLoader()

        initUnitOfMeasurement()
        initForecastedWeatherList()
        initWeatherDetailsList()
        binding.chipGroup.check(R.id.chip_today)
        getForecastForLocation()
        checkIfLocationIsInFavorites()
    }

    private fun checkIfLocationIsInFavorites() {
        locationDetailsViewModel.locationId?.let { locationId ->
            locationDetailsViewModel.getIfLocationInFavorites(locationId)
        }
    }

    private fun observeData() {
        observeLoader()
        observeFavoriteLocationOperations()
        observeForecastData()
    }

    private fun observeLoader() {
        locationDetailsViewModel.isLoading
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
        locationDetailsViewModel.locationIsInFavorites
            .observe(viewLifecycleOwner, { locationIsInFavorites ->
                when (locationIsInFavorites) {
                    true -> {
                        setFavoriteIconTint(resources.getColor(R.color.favorite_icon_color, null))
                    }
                    false -> {
                        setFavoriteIconTint(resources.getColor(R.color.medium_gray, null))
                    }
                }
            })

        locationDetailsViewModel.locationAddedToFavorites
            .observe(viewLifecycleOwner, { locationAddedToFavorites ->
                when (locationAddedToFavorites) {
                    true -> {
                        setFavoriteIconTint(resources.getColor(R.color.favorite_icon_color, null))
                        showToastMessage(getString(R.string.toast_location_added_to_favorites))
                        // Reset the flag
                        locationDetailsViewModel.locationAddedToFavorites.value = false
                    }
                }
            })

        locationDetailsViewModel.locationRemovedFromFavorites
            .observe(viewLifecycleOwner, { locationRemovedFromFavorites ->
                when (locationRemovedFromFavorites) {
                    true -> {
                        setFavoriteIconTint(resources.getColor(R.color.medium_gray, null))
                        showToastMessage(getString(R.string.toast_location_removed_from_favorites))
                        // Reset the flag
                        locationDetailsViewModel.locationRemovedFromFavorites.value = false
                    }
                }
            })
    }

    private fun observeForecastData() {
        locationDetailsViewModel.fiveDayForecastSavedLocally
            .observe(viewLifecycleOwner, { fiveDayForecastSavedLocally ->
                when (fiveDayForecastSavedLocally) {
                    true -> {
                        Log.d("DEBUG", "fiveDayForecastSavedLocally")
                    }
                }
            })

        locationDetailsViewModel.forecastData
            .observe(viewLifecycleOwner, { fiveDayForecastEntity ->

                locationDetailsViewModel.fiveDayForecastEntity = fiveDayForecastEntity

                setListsByCategory(fiveDayForecastEntity)

                setLocationName(fiveDayForecastEntity)
                setSunriseTime(fiveDayForecastEntity)
                setSunsetTime(fiveDayForecastEntity)

                setWeatherForToday()

                // Make the main view items visible if they were hidden in case of user saw the No cached data view
                showMainViewItems()
                hideLoader()

                // After showing current data on the UI we update the forecast for the location locally
                locationDetailsViewModel.saveFiveDayForecastForLocation(fiveDayForecastEntity)
            })

        locationDetailsViewModel.updatedForecastData
            .observe(viewLifecycleOwner, { fiveDayForecastEntity ->

                locationDetailsViewModel.fiveDayForecastEntity = fiveDayForecastEntity

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

                showToastMessage(getString(R.string.toast_location_details_updated))

                // After showing current data on the UI we update the forecast for the location locally
                locationDetailsViewModel.saveFiveDayForecastForLocation(fiveDayForecastEntity)
            })

        locationDetailsViewModel.noForecastDataCachedForThisLocation
            .observe(viewLifecycleOwner, { noForecastDataCachedForThisLocation ->
                when (noForecastDataCachedForThisLocation) {
                    true -> {
                        hideMainViewItems()
                        showNoCachedDataPlaceholder(show = true)
                    }
                }
            })
    }

    private fun showNoCachedDataPlaceholder(show: Boolean) {
        binding.noDataCachedPlaceholder.noCachedDataLayout.visibility =
            if (show) View.VISIBLE else View.GONE
    }

    private fun setFavoriteIconTint(colorResource: Int) {
        binding.toolbar.menu.findItem(R.id.add_to_favorites).icon.setTint(colorResource)
    }

    private fun hideMainViewItems() {
        binding.toolbar.menu.findItem(R.id.add_to_favorites).isVisible = false
        binding.toolbar.menu.findItem(R.id.refresh_data_for_this_location).isVisible =
            false
        binding.chipGroup.visibility = View.GONE
        showCards(show = false)
    }

    private fun showCards(show: Boolean) {
        binding.mainCardView.visibility = if (show) View.VISIBLE else View.GONE
        binding.detailsCardView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showMainViewItems() {
        binding.toolbar.menu.findItem(R.id.add_to_favorites).isVisible = true
        binding.toolbar.menu.findItem(R.id.refresh_data_for_this_location).isVisible =
            true
        binding.chipGroup.visibility = View.VISIBLE
        showCards(show = true)
        showNoCachedDataPlaceholder(show = false)
    }

    private fun resetListSelectionAfterDataRefresh() {
        locationWeatherForecastAdapter?.selectedPos = FIRST_ITEM_POSITION
        locationWeatherForecastAdapter?.notifyDataSetChanged()
        binding.forecastWeather.smoothScrollToPosition(FIRST_ITEM_POSITION)
    }

    private fun setWeatherForTomorrow() {
        locationDetailsViewModel.tomorrowForecastWeatherList?.let {
            setHourlyForecastForSelectedTimePeriod(
                it
            )
        }
        val selectedWeatherListElementEntityFromList =
            locationDetailsViewModel.tomorrowForecastWeatherList?.firstOrNull()
        selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
            showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
        }
    }

    private fun setWeatherForToday() {
        locationDetailsViewModel.todayForecastWeatherList?.let {
            setHourlyForecastForSelectedTimePeriod(
                it
            )
        }
        val selectedWeatherListElementEntityFromList =
            locationDetailsViewModel.todayForecastWeatherList?.firstOrNull()
        selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
            showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
        }
    }

    private fun setClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_to_favorites -> {
                    locationDetailsViewModel.locationId?.let { locationId ->
                        when (locationDetailsViewModel.locationIsInFavorites.value) {
                            true -> {
                                // Location is already in favorites, we need to delete it
                                locationDetailsViewModel.removeLocationWeatherForecastFromFavorites(
                                    locationId
                                )
                            }
                            else -> {
                                // Location is not in favorites yet, get and store the locationWithWeatherEntity,
                                // and the forecast for it locally
                                locationDetailsViewModel.fiveDayForecastEntity?.let { fiveDayForecastEntity ->
                                    locationDetailsViewModel.getLocationWithWeatherAndSaveLocal(
                                        locationId, fiveDayForecastEntity
                                    )
                                }.orElse {
                                    Log.d(
                                        "DEBUG",
                                        "locationDetailsViewModel.fiveDayForecastEntity is NULL"
                                    )
                                }
                            }
                        }
                    }.orElse {
                        showGeneralErrorToastMessage()
                    }
                }
                R.id.refresh_data_for_this_location -> {
                    locationDetailsViewModel.locationId?.let { locationId ->
                        if (locationDetailsViewModel.isDeviceConnectedToNetwork(requireContext())) {
                            locationDetailsViewModel.updateLocationWeatherForecast(
                                locationId
                            )
                        } else {
                            showToastMessage(getString(R.string.error_no_internet_message))
                        }
                    }.orElse {
                        showGeneralErrorToastMessage()
                    }
                }
            }
            true
        }
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            // Responds to child chip checked/unchecked
            when (checkedId) {
                R.id.chip_today -> {
                    locationWeatherForecastAdapter?.isShowingDays = false
                    val selectedWeatherListElementEntityFromList =
                        locationDetailsViewModel.todayForecastWeatherList?.firstOrNull()
                    selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
                        showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
                    }
                    locationWeatherForecastAdapter?.setDataSet(locationDetailsViewModel.todayForecastWeatherList as MutableList<WeatherListElementEntity>)
                    selectFirstForecastItem()
                }
                R.id.chip_tomorrow -> {
                    locationWeatherForecastAdapter?.isShowingDays = false
                    val selectedWeatherListElementEntityFromList =
                        locationDetailsViewModel.tomorrowForecastWeatherList?.firstOrNull()
                    selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
                        showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
                    }
                    locationWeatherForecastAdapter?.setDataSet(locationDetailsViewModel.tomorrowForecastWeatherList as MutableList<WeatherListElementEntity>)
                    selectFirstForecastItem()
                }
                R.id.chip_next_five_days -> {
                    locationWeatherForecastAdapter?.isShowingDays = true
                    setWeatherForNextFiveDays()
                    locationWeatherForecastAdapter?.setDataSet(locationDetailsViewModel.forecastDailyList as MutableList<WeatherListElementEntity>)
                    selectFirstForecastItem()
                }
            }
        }

        locationWeatherForecastAdapter?.setOnItemClickedListener { selectedWeatherListElementEntity ->
            selectedWeatherListElementEntity?.let {
                showDetailsForSelectedWeatherListItem(it)
            }
        }

        binding.noDataCachedPlaceholder.locationDetailsNoDataCachedRetryButton.setOnClickListener {
            if (locationDetailsViewModel.isDeviceConnectedToNetwork(requireContext())) {
                getForecastForLocation()
            } else {
                showToastMessage(getString(R.string.error_still_not_connected))
            }
        }
    }

    private fun getForecastForLocation() {
        locationDetailsViewModel.locationId?.let { locationId ->
            locationDetailsViewModel.getLocationWeatherForecastByLocationId(
                requireContext(),
                locationId
            )
        }.orElse {
            showGeneralErrorToastMessage()
        }
    }

    private fun setWeatherForNextFiveDays() {
        val selectedWeatherListElementEntityFromList =
            locationDetailsViewModel.forecastDailyList?.firstOrNull()
        selectedWeatherListElementEntityFromList?.let { selectedWeatherListElementEntity ->
            showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity)
        }
    }

    private fun selectFirstForecastItem() {
        locationWeatherForecastAdapter?.resetListSelection()
        binding.forecastWeather.scrollToPosition(FIRST_ITEM_POSITION)
    }

    private fun initWeatherDetailsList() {
        binding.weatherAttributesList.layoutManager = LinearLayoutManager(requireContext())
        binding.weatherAttributesList.adapter = weatherDetailsAdapter
    }

    private fun initForecastedWeatherList() {
        locationWeatherForecastAdapter =
            LocationWeatherForecastAdapter(requireContext(), selectedUnitOfMeasurement)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.forecastWeather.layoutManager = layoutManager
        binding.forecastWeather.adapter = locationWeatherForecastAdapter
    }

    private fun showDetailsForSelectedWeatherListItem(selectedWeatherListElementEntity: WeatherListElementEntity) {
        setWeatherIcon(selectedWeatherListElementEntity)
        setWeatherTemperature(selectedWeatherListElementEntity)
        setMinMaxTemperature(selectedWeatherListElementEntity)
        setWeatherDescription(selectedWeatherListElementEntity)
        setDetailsForSelectedForecastItem(selectedWeatherListElementEntity)
    }

    private fun setListsByCategory(fiveDayForecastEntity: FiveDayForecastEntity) {
        locationDetailsViewModel.todayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate -> DateUtils.isToday(predicate.dt) }
        locationDetailsViewModel.tomorrowForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate -> DateUtils.isTomorrow(predicate.dt) }
        locationDetailsViewModel.tomorrowPlus1DayForecastList =
            fiveDayForecastEntity.list?.filter { predicate -> DateUtils.isTomorrowPlus1Day(predicate.dt) }
        locationDetailsViewModel.tomorrowPlus2DayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate ->
                DateUtils.isTomorrowPlus2Days(
                    predicate.dt
                )
            }
        locationDetailsViewModel.tomorrowPlus3DayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate ->
                DateUtils.isTomorrowPlus3Days(
                    predicate.dt
                )
            }
        locationDetailsViewModel.tomorrowPlus4DayForecastWeatherList =
            fiveDayForecastEntity.list?.filter { predicate ->
                DateUtils.isTomorrowPlus4Days(
                    predicate.dt
                )
            }

        // We remove today from the list because we want to see the next five days when tapping on the 5 days Chip
        val cleanedListForecastDailyList =
            locationDetailsViewModel.createForecastedWeatherListByDays() as MutableList<WeatherListElementEntity>
        cleanedListForecastDailyList.removeFirst()
        locationDetailsViewModel.forecastDailyList = cleanedListForecastDailyList
    }

    private fun setDetailsForSelectedForecastItem(selectedWeatherListElementEntity: WeatherListElementEntity) {
        weatherDetailsAdapter.setDataSet(
            locationDetailsViewModel.createWeatherAttributeList(
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
}
