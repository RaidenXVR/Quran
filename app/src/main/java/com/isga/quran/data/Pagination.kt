package com.isga.quran.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Pagination(
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("next_page")
    val nextPage:Int,
    @SerializedName("total_page")
    val totalPages: Int,
    @SerializedName("total_records")
    val totalRecords: Int
): Serializable
