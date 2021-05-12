package com.cst.weatherapptest.ui.locationsearch

import android.location.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cst.weatherapptest.databinding.ItemLocationSearchBinding
import com.cst.weatherapptest.ui.base.BaseAdapter

/**
 * Simple adapter to display the search results of Location Search
 */
class LocationSearchAdapter : BaseAdapter<ItemLocationSearchBinding, Address>() {

    override fun provideBinding(parent: ViewGroup): ItemLocationSearchBinding {
        return ItemLocationSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<ItemLocationSearchBinding>, position: Int) {
        super.onBindViewHolder(holder, position)

        data[position].let { address: Address ->
            holder.binding.locationName.text = address.locality
        }
    }
}
