package com.isga.quran.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.R
import com.isga.quran.data.Surah

class SurahAdapter(
    private val surah: List<Surah>,
    private val onSurahClick: (Surah) -> Unit
) :

    RecyclerView.Adapter<SurahAdapter.ChapterViewHolder>() {

    // ViewHolder class for item views
    class ChapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var id: Int = 0
        val chapterName: TextView = view.findViewById(R.id.tvChapterName)
        val translation: TextView = view.findViewById(R.id.tvTranslation)
        val surahNumber: TextView = view.findViewById(R.id.surah_number)

        fun bind(chapter: Surah, onSurahClick: (Surah) -> Unit) {
            chapterName.text = chapter.transliteration
            translation.text = chapter.translation
            surahNumber.text = chapter.id.toString()

            itemView.setOnClickListener {
                onSurahClick(chapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ChapterViewHolder(view)

    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = surah[position]
        holder.id = chapter.id
        holder.bind(chapter, onSurahClick)

    }

    override fun getItemCount() = surah.size

}
