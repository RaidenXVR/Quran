package com.isga.quran.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AudioVerse(
    @SerializedName("verse_key")
    val verseKey: String,
    val url: String
): Serializable


