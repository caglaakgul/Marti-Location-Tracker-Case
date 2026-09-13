package com.caglaakgul.martilocationtrackercase.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResultDto(
    @SerializedName("formatted_address")
    val formattedAddress: String = ""
)