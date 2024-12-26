package com.isga.quran.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isga.quran.data.Surah

var surahList: List<Surah> = listOf()

fun assetJSONReader(context: Context, filename: String): String {
    return context.assets.open(filename).bufferedReader().use { it.readText() }

}

fun parseSurah(context: Context): List<Surah> {
    val sharedPreferences = context.getSharedPreferences("QuranAppPreferences", MODE_PRIVATE)
    val lang = when (sharedPreferences.getInt("language", 0)){
        0->"id"
        1->"bn"
        2-> "en"
        3-> "es"
        4-> "fr"
        5-> "ru"
        6-> "sv"
        7-> "tr"
        8-> "ur"
        9->"zh"
        else -> "en"
    }
    val jsonString = assetJSONReader(context, "quran_${lang}.json")
    val surahListType = object : TypeToken<List<Surah>>() {}.type
    surahList = Gson().fromJson(jsonString, surahListType)
    return surahList
}