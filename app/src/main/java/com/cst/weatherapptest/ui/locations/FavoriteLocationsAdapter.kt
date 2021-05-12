package com.cst.weatherapptest.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.weatherapptest.databinding.LocationListItemBinding
import com.cst.weatherapptest.ui.base.BaseAdapter
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.setTextUnitConverted
import java.util.*

/**
 * Adapter class based on the BaseAdapter (which is extended from RecyclerView.Adapter)
 * Replaced to use FavoriteLocationsListAdapter (extended from ListAdapter) because it's better performance
 *
 * If some errors occurs with the FavoriteLocationsListAdapter,
 * it easily can be replaced to this "older" implementation.
 */
class FavoriteLocationsAdapter(
    val context: Context,
    val selectedUnitOfMeasurement: UnitOfMeasurement
) :
    BaseAdapter<LocationListItemBinding, LocationWithWeatherEntity>(), Filterable {

    override fun provideBinding(parent: ViewGroup): LocationListItemBinding {
        return LocationListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder<LocationListItemBinding>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)

        initFavoriteLocationListItem(holder.binding, data[position])
    }

    private fun initFavoriteLocationListItem(
        binding: LocationListItemBinding,
        locationWithWeatherEntity: LocationWithWeatherEntity
    ) {
        initWeatherIcon(binding, locationWithWeatherEntity)
        initLocationName(binding, locationWithWeatherEntity)
        initLocationWeatherDescription(binding, locationWithWeatherEntity)
        initLocationWeatherTemperature(binding, locationWithWeatherEntity)
    }

    private fun initWeatherIcon(
        binding: LocationListItemBinding,
        locationWithWeatherEntity: LocationWithWeatherEntity
    ) {
        locationWithWeatherEntity.weatherResponseData?.first()?.icon?.let { iconIdentifier ->
            Glide.with(context).load(Utils.getWeatherTypeIconByIdentifier(context, iconIdentifier))
                .into(binding.locationWeatherIcon)
        }
    }

    private fun initLocationName(
        binding: LocationListItemBinding,
        locationWithWeatherEntity: LocationWithWeatherEntity
    ) {
        locationWithWeatherEntity.name?.let { name ->
            binding.locationName.text = name
        }
    }

    private fun initLocationWeatherDescription(
        binding: LocationListItemBinding,
        locationWithWeatherEntity: LocationWithWeatherEntity
    ) {
        locationWithWeatherEntity.weatherResponseData?.firstOrNull()?.description?.let { description ->
            binding.locationWeatherDescription.text = description
        }
    }

    private fun initLocationWeatherTemperature(
        binding: LocationListItemBinding,
        locationWithWeatherEntity: LocationWithWeatherEntity
    ) {
        locationWithWeatherEntity.main?.temp?.let { temperature ->
            binding.locationTemperature.setTextUnitConverted(
                temperature,
                selectedUnitOfMeasurement
            )
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredResults: MutableList<LocationWithWeatherEntity> = when {
                    constraint.isEmpty() -> copyData
                    else -> getFilteredResults(constraint.toString().toLowerCase(Locale.ROOT))
                }

                val results = FilterResults()
                results.values = filteredResults

                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                data = results.values as MutableList<LocationWithWeatherEntity>
                notifyDataSetChanged()
            }
        }
    }

    private fun getFilteredResults(constraint: String): MutableList<LocationWithWeatherEntity> {
        return copyData
            .filter { it.name?.toLowerCase(Locale.ROOT).orEmpty().contains(constraint) }
            .toMutableList()
    }
}
