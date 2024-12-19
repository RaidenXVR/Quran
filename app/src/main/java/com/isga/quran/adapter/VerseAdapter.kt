package com.isga.quran.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUSABLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.R
import com.isga.quran.data.Surah
import com.isga.quran.data.Verse
import com.isga.quran.utils.fetchVerseInSurah
import com.isga.quran.utils.parseSurah
import com.isga.quran.utils.surahList

class VerseAdapter(
    private val surah: Surah,
    private val context: Context
) : RecyclerView.Adapter<VerseAdapter.VerseViewHolder>() {

    class VerseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val verseText: TextView = view.findViewById(R.id.tvVerseText)
        private val verseTranslation: TextView = view.findViewById(R.id.tvVerseTranslation)
        fun bind(surahId: Int, verseId: Int = 1, context: Context) {
            val verse: Verse = if (surahList.isNotEmpty()) {
                Log.d("get from surahList", surahList[surahId-1].name)
                surahList[surahId - 1].verses[verseId - 1]
            } else {
                parseSurah(context)[surahId - 1].verses[verseId - 1]
            }
            verseText.text = verse.text
            verseTranslation.text = verse.translation
            itemView.focusable = FOCUSABLE
            itemView.setOnClickListener {
                fetchVerseInSurah("2", surahId, verseId, context)

            }
            itemView.setOnLongClickListener {


                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verse, parent, false)
        return VerseViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        holder.bind( surah.id,position + 1, context)
    }

    override fun getItemCount() = surah.verses.size
}
