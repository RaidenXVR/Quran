package com.isga.quran

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.adapter.VerseAdapter
import com.isga.quran.data.Surah
import com.isga.quran.utils.AudioManager
import com.isga.quran.utils.fetchAllVerseInSurah

class VerseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_surah)


        // Retrieve the Chapter object
        val chapter = intent.getSerializableExtra("verses") as? Surah
        val clickedVerse = intent.getIntExtra("bookmarked_verse", 1)



        // Display the chapter name and verses
        val chapterTitle: TextView = findViewById(R.id.tvSurahName)
        if (chapter != null) {
            chapterTitle.text = chapter.transliteration

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSurah)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = VerseAdapter(chapter, this)

            recyclerView.scrollToPosition(clickedVerse - 1)

            // all verse in surah
            val listenAllButton: ImageButton = findViewById(R.id.listen_surah)
            listenAllButton.setOnClickListener {
                val preferences = getSharedPreferences("QuranAppPreferences", MODE_PRIVATE)
                val reciterId = when (preferences.getInt("voice", 0)) {
                    0 -> "2"
                    1 -> "7"
                    2 -> "5"
                    3 -> "4"
                    4 -> "3"
                    else -> "2"
                }

                fetchAllVerseInSurah(reciterId, chapter.id, this,{ result ->
                    if (result) {
                        listenAllButton.setBackgroundResource(R.drawable.ic_stop)
                        listenAllButton.setOnClickListener {
                            AudioManager.stopAudio()
                            listenAllButton.setBackgroundResource(R.drawable.ic_play)
                        }
                    } else {
                        listenAllButton.setBackgroundResource(R.drawable.ic_play)
                    }
                }
                ){position->
                    recyclerView.scrollToPosition(position)
                    if (position >= chapter.verses.size-1){
                        listenAllButton.setBackgroundResource(R.drawable.ic_play)

                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.surahMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }


}