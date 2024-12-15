package com.isga.quran.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.R
import com.isga.quran.data.Verse

class VerseAdapter(private val verses: List<Verse>) : RecyclerView.Adapter<VerseAdapter.VerseViewHolder>() {

    class VerseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val verseText: TextView = view.findViewById(R.id.tvVerseText)
        val verseTranslation: TextView = view.findViewById(R.id.tvVerseTranslation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verse, parent, false)
        return VerseViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        val verse = verses[position]
        holder.verseText.text = verse.text
        holder.verseTranslation.text = verse.translation
    }

    override fun getItemCount() = verses.size
}
