package com.cst.weatherapptest.ui.locationdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cst.domain.models.forecast.WeatherListElementEntity
import com.cst.weatherapptest.databinding.ItemWeatherForecastBinding
import com.cst.weatherapptest.ui.base.BaseAdapter
import com.cst.weatherapptest.util.DateUtils
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.enums.UnitOfMeasurement

class LocationWeatherForecastAdapter(
    val context: Context,
    val selectedUnitOfMeasurement: UnitOfMeasurement
) :
    BaseAdapter<ItemWeatherForecastBinding, WeatherListElementEntity>() {

    companion object {
        private const val FIRST_ITEM_POSITION = 0
    }

    var isShowingDays = false
    var selectedPos = FIRST_ITEM_POSITION

    override fun provideBinding(parent: ViewGroup): ItemWeatherForecastBinding {
        return ItemWeatherForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<ItemWeatherForecastBinding>, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.itemView.isSelected = selectedPos == position

        data[position].let { weatherListElementEntity ->

            initForecastItemSelection(holder, position, weatherListElementEntity)

            with(holder.binding) {
                setTimeOfDataForecasted(this, weatherListElementEntity, position)
                setForecastIcon(this, weatherListElementEntity)
                setTemperature(this, weatherListElementEntity)
                setHumidity(this, weatherListElementEntity)
            }
        }
    }

    private fun initForecastItemSelection(
        holder: ViewHolder<ItemWeatherForecastBinding>,
        position: Int,
        weatherListElementEntity: WeatherListElementEntity
    ) {
        holder.itemView.setOnClickListener {
            if (selectedPos >= FIRST_ITEM_POSITION)
                notifyItemChanged(selectedPos)
            selectedPos = holder.bindingAdapterPosition
            notifyItemChanged(position)
            clickListener?.invoke(weatherListElementEntity)
        }
    }

    private fun setTimeOfDataForecasted(
        binding: ItemWeatherForecastBinding,
        weatherListElementEntity: WeatherListElementEntity,
        position: Int
    ) {
        weatherListElementEntity.dt.let { timeOfDataForecasted ->
            when (isShowingDays) {
                true -> {
                    binding.hour.text =
                        DateUtils.convertLongToHour24hWithDay(timeOfDataForecasted)
                }
                else -> {
                    val lastItemPosition = this.itemCount - 1
                    binding.hour.text =
                        if (position == lastItemPosition) DateUtils.convertLongToHour24hIfLastItem(
                            timeOfDataForecasted
                        ) else DateUtils.convertLongToTime(timeOfDataForecasted)
                }
            }
        }
    }

    private fun setForecastIcon(
        binding: ItemWeatherForecastBinding,
        weatherListElementEntity: WeatherListElementEntity,
    ) {
        weatherListElementEntity.weather?.firstOrNull()?.icon?.let { iconIdentifier ->
            Glide.with(context).load(Utils.getWeatherTypeIconByIdentifier(context, iconIdentifier))
                .into(binding.weatherIcon)
        }
    }

    private fun setTemperature(
        binding: ItemWeatherForecastBinding,
        weatherListElementEntity: WeatherListElementEntity
    ) {
        weatherListElementEntity.main?.temp?.let { temp ->
            binding.weatherTemperature.text =
                Utils.getTemperatureWithUnit(temp, selectedUnitOfMeasurement)
        }
    }

    private fun setHumidity(
        binding: ItemWeatherForecastBinding,
        weatherListElementEntity: WeatherListElementEntity,
    ) {
        weatherListElementEntity.main?.humidity?.let { humidity ->
            val humidityWithUnit = "$humidity ${UnitOfMeasurement.UNIT_OF_MEASURE_HUMIDITY}"
            binding.weatherHumidityValue.text = humidityWithUnit
        }
    }

    fun resetListSelection() {
        selectedPos = 0
    }
}
