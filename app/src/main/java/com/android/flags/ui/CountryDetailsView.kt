package com.android.flags.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.flags.R
import com.android.flags.data.responses.CountryResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

class CountryDetailsView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var ivFlag: ImageView
    private var tvName: TextView
    private var tvCapital: TextView
    private var tvRegion: TextView
    private var tvSubregion: TextView
    private var tvPopulation: TextView

    init {
        View.inflate(context, R.layout.view_country_details, this)

        ivFlag = findViewById(R.id.ivFlag)
        tvName = findViewById(R.id.tvName)
        tvCapital = findViewById(R.id.tvCapital)
        tvRegion = findViewById(R.id.tvRegion)
        tvSubregion = findViewById(R.id.tvSubregion)
        tvPopulation = findViewById(R.id.tvPopulation)
    }

    fun setData(country: CountryResponse) {
        Glide.with(context).load(country.flags?.png).into(ivFlag)
        tvName.text = country.name?.common
        tvCapital.text = country.capital?.get(0)
        tvRegion.text = country.region
        tvSubregion.text = country.subregion
        tvPopulation.text = country.population.toString()
    }
}