package com.android.flags.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.flags.R
import com.android.flags.data.responses.CountryResponse
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class CountryAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {
    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<CountryResponse>() {
        override fun areItemsTheSame(oldItem: CountryResponse, newItem: CountryResponse): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: CountryResponse,
            newItem: CountryResponse
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var countries: List<CountryResponse>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        return CountryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.flag_item, parent, false)
        )
    }

    private var onItemClickListener: ((CountryResponse) -> Unit)? = null

    fun setOnItemClickListener(listener: (CountryResponse) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val url = countries[position].flags?.png
        holder.itemView.let {
            glide.load(url).into(it.findViewById(R.id.ivFlag))
            it.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(countries[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }
}