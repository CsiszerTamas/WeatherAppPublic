package com.cst.weatherapptest.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.weatherapptest.databinding.LocationListItemBinding
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.setTextUnitConverted
import java.util.*
import kotlin.collections.ArrayList

/**
 * Adapter for displaying the Favorite locations
 * It is used because it offers better performance and animations than regular
 * FavoriteLocationsAdapter (which is extended from RecyclerView.Adapter)
 */
class FavoriteLocationsListAdapter(
    val context: Context,
    val selectedUnitOfMeasurement: UnitOfMeasurement
) : ListAdapter<LocationWithWeatherEntity, FavoriteLocationsListAdapter.LocationViewHolder>(
    LocationDiffCallback()
), Filterable {

    private var clickListener: ((item: LocationWithWeatherEntity?) -> Unit)? = null

    // Used in Filters
    var copyData: MutableList<LocationWithWeatherEntity> = ArrayList()
    var data: MutableList<LocationWithWeatherEntity> = ArrayList()

    fun getDataSet() = data

    fun setDataSet(newData: MutableList<LocationWithWeatherEntity>) {
        data = newData
        copyData = data.toMutableList()
        // Should clear the list before submitting the new one to prevent exception:
        // Inconsistency detected. Invalid item position error
        submitList(null)
        submitList(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class LocationViewHolder(
        val binding: LocationListItemBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        initFavoriteLocationListItem(holder.binding, data[position])
        holder.binding.itemView.setOnClickListener {
            clickListener?.invoke(data[position])
        }
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

    fun setOnItemClickedListener(listener: ((item: LocationWithWeatherEntity?) -> Unit)?) {
        clickListener = listener
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


private class LocationDiffCallback : DiffUtil.ItemCallback<LocationWithWeatherEntity>() {

    override fun areItemsTheSame(
        oldItem: LocationWithWeatherEntity,
        newItem: LocationWithWeatherEntity
    ): Boolean {
        return oldItem.id == newItem.id && oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: LocationWithWeatherEntity,
        newItem: LocationWithWeatherEntity
    ): Boolean {
        return oldItem == newItem
    }
}
