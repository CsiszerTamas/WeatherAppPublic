package com.cst.weatherapptest.ui.settings

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.cst.weatherapptest.R
import com.cst.weatherapptest.databinding.FragmentSettingsBinding
import com.cst.weatherapptest.service.WeatherSyncJobService
import com.cst.weatherapptest.ui.MainActivity
import com.cst.weatherapptest.ui.base.BaseFragment
import com.cst.weatherapptest.util.enums.DataSync
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.storage.PreferenceHelper
import com.cst.weatherapptest.util.storage.PreferenceHelper.get
import com.cst.weatherapptest.util.storage.PreferenceHelper.set
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.system.exitProcess

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
    R.layout.fragment_settings
) {
    companion object {
        private const val DEFAULT_PROCESS_ID = 0
    }

    @Inject
    lateinit var preferenceHelper: SharedPreferences

    override fun hideLoader() {
    }

    override fun showLoader() {
    }

    override fun viewModelClass(): KClass<SettingsViewModel> = SettingsViewModel::class

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        initUnitOfMeasurementExposedDropdown()
        initBackgroundDataSyncExposedDropdown()
        initThemeSelectionSwitch()
    }

    private fun initUnitOfMeasurementExposedDropdown() {
        // Initialising the Exposed Dropdowns adapter
        val items = resources.getStringArray(R.array.units_array)
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
        binding.exposedDropdownUnitAutocompleteTextview.setAdapter(adapter)

        // Get the exposed dropdown value from SharedPreferences
        val unitOfMeasurementOrdinal: Int? =
            preferenceHelper[PreferenceHelper.SELECTED_UNIT_OF_MEASUREMENT, 0]

        unitOfMeasurementOrdinal?.let {
            val unitOfMeasurment: String =
                resources.getStringArray(R.array.units_array)[it]
            binding.exposedDropdownUnitAutocompleteTextview.setText(unitOfMeasurment, false)
        }

        // Save the new UnitOfMeasurement value to SharedPreferences
        binding.exposedDropdownUnitAutocompleteTextview.setOnItemClickListener { parent, view, position, id ->
            preferenceHelper[PreferenceHelper.SELECTED_UNIT_OF_MEASUREMENT] =
                UnitOfMeasurement.valueOf(position).ordinal
        }
    }

    private fun initBackgroundDataSyncExposedDropdown() {
        val items = resources.getStringArray(R.array.auto_refresh_array)
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
        binding.backgroundSyncAutocompleteTextView.setAdapter(adapter)

        // Get the get the selected data sync scheduling value from SharedPreferences
        val selectedBackgroundDataSyncIntervalOrdinal: Int? =
            preferenceHelper[PreferenceHelper.SELECTED_DATA_REFRESH_INTERVAL, 0]

        selectedBackgroundDataSyncIntervalOrdinal?.let {
            val autoRefresh: String = resources.getStringArray(R.array.auto_refresh_array)[it]
            binding.backgroundSyncAutocompleteTextView.setText(autoRefresh, false)
        }

        // Save the newly selected data sync scheduling value to SharedPreferences
        binding.backgroundSyncAutocompleteTextView.setOnItemClickListener { parent, view, position, id ->
            preferenceHelper[PreferenceHelper.SELECTED_DATA_REFRESH_INTERVAL] =
                DataSync.valueOf(position).ordinal

            val autoRefresh = DataSync.valueOf(position)
            scheduleWeatherSync(autoRefresh)
        }
    }

    private fun initThemeSelectionSwitch() {
        when (getSavedNightMode()) {
            MODE_NIGHT_NO -> {
                // Light Mode
                binding.settingsDarkModeSwitch.isChecked = false
            }
            MODE_NIGHT_YES -> {
                // Dark Mode
                binding.settingsDarkModeSwitch.isChecked = true
            }
        }

        binding.settingsDarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                false -> {
                    saveDarkModeSettings(MODE_NIGHT_NO)
                    restartAppToApplyChanges()
                }
                true -> {
                    saveDarkModeSettings(MODE_NIGHT_YES)
                    restartAppToApplyChanges()
                }
            }
        }
    }

    private fun saveDarkModeSettings(code: Int) {
        preferenceHelper[PreferenceHelper.SELECTED_THEME] = code
    }

    private fun getSavedNightMode(): Int? {
        return preferenceHelper[PreferenceHelper.SELECTED_THEME, 0]
    }

    private fun restartAppToApplyChanges() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_fragment_dark_mode_delete_dialog_title_text))
            .setMessage(getString(R.string.settings_fragment_dark_mode_delete_dialog_message_text))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.settings_fragment_dark_mode_delete_dialog_positive_button_text)) { _, _ ->
                startActivity(
                    Intent(
                        requireActivity().applicationContext,
                        MainActivity::class.java
                    )
                )
                exitProcess(DEFAULT_PROCESS_ID)
            }.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.cta1, requireActivity().theme))
    }

    private fun scheduleWeatherSync(dataSync: DataSync) {
        when (dataSync) {
            DataSync.NEVER -> WeatherSyncJobService.cancel(requireContext())
            else -> WeatherSyncJobService.schedule(
                requireContext(),
                dataSync.value
            )
        }
    }
}
