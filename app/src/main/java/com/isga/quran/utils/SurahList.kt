package com.isga.quran.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isga.quran.data.Surah

var surahList: List<Surah> = listOf()

fun assetJSONReader(context: Context, filename: String): String {
    return context.assets.open(filename).bufferedReader().use { it.readText() }

}

fun parseSurah(context: Context): List<Surah> {

    val jsonString = assetJSONReader(context, "quran_en.json")
    val surahListType = object : TypeToken<List<Surah>>() {}.type
    surahList = Gson().fromJson(jsonString, surahListType)
    return surahList
}