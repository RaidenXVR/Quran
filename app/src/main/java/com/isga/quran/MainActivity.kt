package com.isga.quran

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.isga.quran.utils.FirestoreInstance
import com.isga.quran.utils.parseSurah

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
            intent.putExtra("verses", clickedSurah)
            startActivity(intent)
        }

        // Menambahkan tombol navigasi ke LoginActivity
        //TODO: Not Yet Implemented <Login Button>
//        val navigateToLoginButton = findViewById<Button>(R.id.navigateToLoginButton)
//        navigateToLoginButton.setOnClickListener {
//            val intent = Intent(this, LogoutActivity::class.java)
//            startActivity(intent)
//        }

        // Mengatur padding sesuai dengan insets (untuk edge-to-edge UI)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}