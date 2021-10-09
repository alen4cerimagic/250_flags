package com.android.flags.remote

import com.android.flags.domain.CountryModel

data class CountryDto(
    val flags: Flags?,
    val name: Name?,
    val region: String?
)

data class Flags(
    val png: String?
)

data class Name(
    val common: String?
)

fun CountryDto.toCountryModel(): CountryModel {
    return CountryModel(
        name = name!!.common!!,
        region = region,
        flag = flags!!.png!!,
        null
    )
}