package com.isga.quran.utils

import android.media.MediaPlayer
import android.util.Log
import java.io.File

object AudioManager {
    var mediaPlayer: MediaPlayer? = null
    var audioFiles: MutableList<File>? = null
    var audioIndex: Int = 0

    fun playAudio(filePath: String) {
        // Stop any currently playing audio
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }

        // Initialize and play the new audio
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
    }

    fun stopAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
            audioIndex = 0
            audioFiles = null
        }
    }

    fun playAudios(files: MutableList<File>, audioFinishedCallback: (Int) -> Unit) {
        // Initialize playback data
        audioFiles = files
        audioIndex = 0

        // Play the first audio
        playCurrentAudio(audioFinishedCallback)
    }

    private fun playCurrentAudio(audioFinishedCallback: (Int) -> Unit) {
        // Stop and release any existing MediaPlayer
        mediaPlayer?.release()
        audioFinishedCallback(audioIndex)
        // Create a new MediaPlayer instance
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener {
                playNextAudio(audioFinishedCallback)

            }
            try {
                // Set the data source for the current audio file
                setDataSource(audioFiles!![audioIndex].path)
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun playNextAudio(audioFinishedCallback: (Int) -> Unit) {
        audioIndex++
        if (audioIndex < (audioFiles?.size ?: 0)) {
            playCurrentAudio(audioFinishedCallback) // Play the next audio
        } else {
            // Cleanup after all audios are played
            stopAudio()
        }
    }

}
