package com.isga.quran

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isga.quran.adapter.SurahAdapter
import com.isga.quran.data.Surah

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val surah: List<Surah> = parseSurah(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SurahAdapter(surah) { clickedSurah ->
            Toast.makeText(this, "Clicked: ${clickedSurah.transliteration}", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this, VerseActivity::class.java)
            val surahArrayList: Array<Surah> = surah.toTypedArray()
            intent.putExtra("verses", surahArrayList)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun assetJSONReader(context: Context, filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }

    }

    fun parseSurah(context: Context): List<Surah> {
        val jsonString = assetJSONReader(context, "quran_en.json")
        val surahListType = object : TypeToken<List<Surah>>() {}.type
        return Gson().fromJson(jsonString, surahListType)
    }
}