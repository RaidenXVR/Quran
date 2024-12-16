package com.isga.quran.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Objects

data class Meta(
    @SerializedName("reciter_name")
    val reciterName: String,
    @SerializedName("recitation_style")
    val recitationStyle: String?,
    val filters: Objects?
): Serializable
