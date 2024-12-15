package com.isga.quran.data

import com.google.gson.annotations.SerializedName

data class Surah(
    val id: Int,
    val name: String,
    val transliteration: String,
    val translation: String,
    val type: String,
    @SerializedName("total_verses")
    val totalVerses: Int,
    val verses: List<Verses>
)
