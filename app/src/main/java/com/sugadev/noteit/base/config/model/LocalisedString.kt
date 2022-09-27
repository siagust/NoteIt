package com.sugadev.noteit.base.config.model

import com.google.gson.annotations.SerializedName

data class LocalisedString(
    @SerializedName("en") val english: String = "",
    @SerializedName("id") val id: String = ""
)