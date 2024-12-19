package com.isga.quran.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.R
import com.isga.quran.data.Bookmark

class BookmarkAdapter(
    private val bookmarks: List<Bookmark>
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    // ViewHolder class untuk item bookmark
    class BookmarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chapterName: TextView = view.findViewById(R.id.tvChapterName)
        val verseText: TextView = view.findViewById(R.id.tvVerseText)

        fun bind(bookmark: Bookmark) {
            chapterName.text = bookmark.chapterName
            verseText.text = bookmark.verseText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarks[position]
        holder.bind(bookmark)
    }

    override fun getItemCount() = bookmarks.size
}
