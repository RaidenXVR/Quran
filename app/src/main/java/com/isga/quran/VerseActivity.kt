package com.isga.quran

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.adapter.SurahAdapter
import com.isga.quran.adapter.VerseAdapter
import com.isga.quran.data.Surah
import com.isga.quran.network.RetrofitClient
import com.isga.quran.utils.downloadFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class VerseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_surah)

        // Retrieve the Chapter object
        val chapter = intent.getSerializableExtra("verses") as? Surah

        // Display the chapter name and verses
        val chapterTitle: TextView = findViewById(R.id.tvSurahName)
        if (chapter != null) {
            chapterTitle.text = chapter.transliteration

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSurah)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = VerseAdapter(surah = chapter, context = applicationContext)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.surahMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }


}