package com.cst.weatherapptest.ui.base

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Common base class of common implementation for an Adapter that can be used for RecyclerView typed lists
 */
abstract class BaseAdapter<B : ViewBinding, I> : RecyclerView.Adapter<BaseAdapter.ViewHolder<B>>() {

    var clickListener: ((item: I?) -> Unit)? = null

    // Used in Filters
    var copyData: MutableList<I> = ArrayList()
    var data: MutableList<I> = ArrayList()

    fun getDataSet() = data

    fun setDataSet(newData: MutableList<I>) {
        data = newData
        copyData = data.toMutableList()
        notifyDataSetChanged()
    }

    abstract fun provideBinding(parent: ViewGroup): B

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        return ViewHolder(provideBinding(parent))
    }

    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        holder.binding.root.setOnClickListener {
            clickListener?.invoke(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnItemClickedListener(listener: ((item: I?) -> Unit)?) {
        clickListener = listener
    }

    class ViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)
}
