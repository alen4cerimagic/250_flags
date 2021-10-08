package com.android.flags.remote.dto

import com.android.flags.domain.model.CountryModel

data class CountryDto(
    val borders: List<String>?,
    val capital: List<String>?,
    val flags: Flags?,
    val independent: Boolean?,
    val landlocked: Boolean?,
    val maps: Maps?,
    val name: Name?,
    val population: Int?,
    val region: String?,
    val status: String?,
    val subregion: String?
)

fun CountryDto.toCountryModel(): CountryModel {
    return CountryModel(
        name = name!!.common!!,
        capital = capital!![0],
        region = region!!,
        flag = flags!!.png!!
    )
}