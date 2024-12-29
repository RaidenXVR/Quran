package com.isga.quran.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.isga.quran.R
import com.isga.quran.custom.LoadingDialog
import com.isga.quran.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Callback
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

fun fetchAllVerseInSurah(recitationId: String, chapterNumber: Int, context: Context, callback: (Boolean)-> Unit, audioFinishedCallback: (Int)->Unit) {
    val loadingDialog = LoadingDialog(context) // Create the loading dialog

    loadingDialog.show() // Show the loading dialog
    val playbackScope = CoroutineScope(Dispatchers.Main)
    loadingDialog.setOnClickCancelButton {
        callback(false)
        playbackScope.cancel()
    }
    playbackScope.launch {
        try {
            Log.d("Voice num", recitationId)

            // Fetch the API response
            val apiResponse = RetrofitClient.instance.getMultiVersesInSurah(recitationId, chapterNumber)
            val files: MutableList<File> = mutableListOf()
            // Parse file name
            for (res in apiResponse.audioFiles) {
                val names = res.url.split("/")
                val filename =
                    if (names.size == 4) "${names[0]}_${names[1]}_${names[3]}" else "${names[0]}_${names[1]}_${names[2]}"
                // Download the file if it doesn't exist
                val file = File(context.filesDir, filename)
                if (!file.exists()) {
                    Log.d("debug download", "Downloaded file: ${filename}")
                    downloadFile(base_url + res.url, file)
                }
                files.add(file)
            }

            // Play the audio files
            AudioManager.playAudios(files, audioFinishedCallback)
            callback(true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Dismiss the loading dialog
            loadingDialog.dismiss()
        }
    }
}

fun fetchVerseInSurah(recitationId: String, chapterNumber: Int, verseNumber: Int, context: Context) {
    val loadingDialog = LoadingDialog(context) // Create the loading dialog
    loadingDialog.show() // Show the loading dialog

    CoroutineScope(Dispatchers.Main).launch {
        try {
            val verseKey = "${chapterNumber}:${verseNumber}"
            Log.d("Voice num", recitationId)

            // Fetch the API response
            val apiResponse = RetrofitClient.instance.getOneVerseInSurah(recitationId, verseKey)

            // Parse file name
            val names = apiResponse.audioFiles[0].url.split("/")
            val filename = if (names.size == 4) "${names[0]}_${names[1]}_${names[3]}" else "${names[0]}_${names[1]}_${names[2]}"
            val file = File(context.filesDir, filename)
            Log.d("debug file", filename)

            // Download the file if it doesn't exist
            if (!file.exists()) {
                Log.d("debug download", "Downloaded file: ${filename}")
                downloadFile(base_url + apiResponse.audioFiles[0].url, file)
            }

            // Play the audio file
            AudioManager.playAudio(file.path)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Dismiss the loading dialog
            loadingDialog.dismiss()
        }
    }
}



