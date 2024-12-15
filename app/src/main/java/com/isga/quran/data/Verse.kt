package com.isga.quran.data

import java.io.Serializable


data class Verse(
    val id: Int,
    val text: String,
    val translation: String
): Serializable
