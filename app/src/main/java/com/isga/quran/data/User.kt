package com.isga.quran.data

import com.google.gson.annotations.SerializedName

data class User(
    val userID: String,
    val bookmarks: List<Bookmark>,
    @SerializedName("last_read")
    val lastRead: Bookmark

)
