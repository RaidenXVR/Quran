package com.isga.quran.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.R
import com.isga.quran.VerseActivity
import com.isga.quran.adapter.BookmarkAdapter
import com.isga.quran.data.Bookmark
import com.isga.quran.utils.FirestoreInstance
import com.isga.quran.utils.bookmarkList
import com.isga.quran.utils.lastRead
import com.isga.quran.utils.surahList

class BookmarkFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.bookmark_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lastReadVerse: TextView = view.findViewById(R.id.last_read_ayat)
        val lastReadSurah: TextView = view.findViewById(R.id.last_read_surah)
        val lastReadCard: CardView = view.findViewById(R.id.last_read_card)
        if (lastRead != null) {
            val lrSurah = surahList[lastRead!!.surahID - 1]
            lastReadVerse.text = lrSurah.verses[lastRead!!.verseID - 1].text
            lastReadSurah.text =
                getString(R.string.verse_info, lrSurah.transliteration, lastRead!!.verseID)
        }

        lastReadCard.setOnClickListener {
            if (lastRead != null) {
                val intent = Intent(context, VerseActivity::class.java)
                intent.putExtra("verses", surahList[lastRead!!.surahID-1])
                intent.putExtra("bookmarked_verse", lastRead!!.verseID)
                startActivity(intent)
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.bookmarkRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BookmarkAdapter(bookmarkList)
    }
}