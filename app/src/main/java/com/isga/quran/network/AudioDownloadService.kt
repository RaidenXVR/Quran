package com.isga.quran.network

import com.google.gson.annotations.SerializedName
import com.isga.quran.data.AudioVerse
import com.isga.quran.data.Meta
import com.isga.quran.data.Pagination
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AudioDownloadService {
    @GET("recitations/{recitation_id}/by_chapter/{chapter_number}")
    suspend fun getMultiVersesInSurah(
        @Path("recitation_id") recitationId: String,
        @Path("chapter_number") chapterNumber: Int
    ): MultiVersesResponse

    @GET("quran/recitations/{recitation_id}")
    suspend fun getOneVerseInSurah(
        @Path("recitation_id") recitationId: String,
        @Query("verse_key") verseKey: String
    ): OneVerseResponse
}

data class MultiVersesResponse(
    @SerializedName("audio_files")
    val audioFiles: List<AudioVerse>,
    val pagination: Pagination)


data class OneVerseResponse(
    @SerializedName("audio_files")
    val audioFiles: List<AudioVerse>,
    val meta: Meta)