package com.android.flags.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.flags.R
import com.android.flags.domain.model.CountryModel
import com.bumptech.glide.Glide

class QuizAdapter : RecyclerView.Adapter<QuizAdapter.CountryViewHolder>() {
    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var countries: List<CountryModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<CountryModel>() {
        override fun areItemsTheSame(oldItem: CountryModel, newItem: CountryModel) = false

        override fun areContentsTheSame(
            oldItem: CountryModel,
            newItem: CountryModel
        ) = false
    })

    private var onItemClickListener: ((CountryModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (CountryModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        return CountryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.flag_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]

        val ivMainFlag = holder.itemView.findViewById<ImageView>(R.id.ivFlagMain)
        val llDetailsHolder = holder.itemView.findViewById<LinearLayout>(R.id.llDetailsHolder)
        val ivSmallFlag = holder.itemView.findViewById<ImageView>(R.id.ivFlagSmall)
        val tvName = holder.itemView.findViewById<TextView>(R.id.tvName)
        val tvCapital = holder.itemView.findViewById<TextView>(R.id.tvCapital)
        val tvRegion = holder.itemView.findViewById<TextView>(R.id.tvRegion)

        ivMainFlag.visibility = View.VISIBLE
        llDetailsHolder.visibility = View.GONE

        Glide.with(holder.itemView.context).load(country.flag).into(ivSmallFlag)
        Glide.with(holder.itemView.context).load(country.flag).into(ivMainFlag)
        tvName.text = country.name
        tvCapital.text = country.capital
        tvRegion.text = country.region

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                ivMainFlag.visibility = View.GONE
                llDetailsHolder.visibility = View.VISIBLE

                click(country)
            }
        }
    }
}