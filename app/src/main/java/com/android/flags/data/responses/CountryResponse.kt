package com.android.flags.data.responses

data class CountryResponse(
    val name: CountryName?,
    val capital: List<String>?,
    val region: String?,
    val subregion: String?,
    val area: Float?,
    val population: Int?,
    val flags: CountryFlag?
)