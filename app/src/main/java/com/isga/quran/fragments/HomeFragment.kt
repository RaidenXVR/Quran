package com.isga.quran.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.R
import com.isga.quran.VerseActivity
import com.isga.quran.adapter.SurahAdapter
import com.isga.quran.data.Surah
import com.isga.quran.utils.parseSurah

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("onCreateView log", "onCreateView called")
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("onViewCreated log", "onViewCreated called")

        // Replace `this` with `requireContext()` and adjust logic for Fragment
        val surah: List<Surah> = parseSurah(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.homeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SurahAdapter(surah) { clickedSurah ->
            Toast.makeText(requireContext(), "Clicked: ${clickedSurah.transliteration}", Toast.LENGTH_SHORT)
                .show()

            val intent = Intent(requireContext(), VerseActivity::class.java)
            intent.putExtra("verses", clickedSurah)
            startActivity(intent)
        }
    }
}
