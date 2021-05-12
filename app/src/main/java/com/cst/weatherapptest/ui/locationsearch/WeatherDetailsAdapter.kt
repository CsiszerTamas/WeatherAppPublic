package com.cst.weatherapptest.ui.locationsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import com.cst.domain.models.items.WeatherAttributeItem
import com.cst.weatherapptest.databinding.ItemWeatherAttributeListBinding
import com.cst.weatherapptest.ui.base.BaseAdapter

/**
 * Simple adapter to display the available weather details data for the search result location
 */
class WeatherDetailsAdapter : BaseAdapter<ItemWeatherAttributeListBinding, WeatherAttributeItem>() {

    override fun provideBinding(parent: ViewGroup): ItemWeatherAttributeListBinding {
        return ItemWeatherAttributeListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder<ItemWeatherAttributeListBinding>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)

        data[position].let { weatherAttributeItem: WeatherAttributeItem ->
            holder.binding.weatherMainHumidityIcon.setImageResource(weatherAttributeItem.iconResourceId)
            holder.binding.weatherMainHumidityLabel.text = weatherAttributeItem.label
            holder.binding.weatherMainHumidityValue.text = weatherAttributeItem.value
        }
    }
}
