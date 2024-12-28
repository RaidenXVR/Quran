package com.isga.quran.data

data class Reminder (
    val name: String,
    val reminderId: Int,
    var hour: Int,
    var minute: Int
)