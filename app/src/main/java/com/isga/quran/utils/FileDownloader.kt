package com.isga.quran.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.isga.quran.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

val base_url: String = "https://verses.quran.com/"

suspend fun downloadFile(url: String, destination: File) {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val inputStream = response.body?.byteStream()
            val outputStream = FileOutputStream(destination)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            throw Exception("Failed to download file: ${response.message}")
        }
    }
}

fun fetchAllVerseInSurah(recitationId: String, chapterNumber: Int) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            // Call the API with dynamic parameters
            val apiResponse = RetrofitClient.instance.getMultiVersesInSurah(recitationId, chapterNumber)
            val audioFilesResponse = apiResponse.audioFiles

            Log.d("audio Downloaded", audioFilesResponse.toString())

//                // Define the destination file
//                val fileName = fileUrl.substringAfterLast("/")
//                val destination = File(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                    fileName
//                )
//
//                // Download the file
//                downloadFile(fileUrl, destination)

//                println("File downloaded to: ${destination.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun fetchVerseInSurah(recitationId: String, chapterNumber: Int, verseNumber:Int, context: Context){
    CoroutineScope(Dispatchers.Main).launch {
        try {
            val verseKey = "${chapterNumber}:${verseNumber}"
            val apiResponse = RetrofitClient.instance.getOneVerseInSurah(recitationId, verseKey)
//            Log.d("URL Delimited",apiResponse.audioFiles[0].url.split("/").toString())
//            Log.d("One Audio Downloaded", apiResponse.audioFiles[0].toString())
            val names = apiResponse.audioFiles[0].url.split("/")
            val filename = "${names[0]}_${names[1]}_${names[3]}"
            val file = File(context.filesDir,filename)
            Log.d("debug file", filename)
            if (!file.exists()) {
                Log.d("debug download", "Downloaded file: ${filename}")
                downloadFile(base_url + apiResponse.audioFiles[0].url, file)
            }
            val mp = MediaPlayer()
            mp.setDataSource(file.path)
            mp.prepare()
            mp.start()

        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }
}