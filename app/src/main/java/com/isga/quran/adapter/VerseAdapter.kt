package com.isga.quran.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUSABLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.rpc.context.AttributeContext.Resource
import com.isga.quran.R
import com.isga.quran.data.Bookmark
import com.isga.quran.data.Surah
import com.isga.quran.data.Verse
import com.isga.quran.utils.UserData
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
        private val verseListen: RelativeLayout = view.findViewById(R.id.verseListen)
        private val verseBookmark: RelativeLayout = view.findViewById(R.id.verseBookmark)
        private val verseLastRead: RelativeLayout = view.findViewById(R.id.verseLastRead)
        fun bind(surahId: Int, verseId: Int = 1, context: Context) {
            val verse: Verse = if (surahList.isNotEmpty()) {
                Log.d("get from surahList", surahList[surahId-1].name)
                surahList[surahId - 1].verses[verseId - 1]
            } else {
                parseSurah(context)[surahId - 1].verses[verseId - 1]
            }
            val baseNum = "FC00".toInt(16)
            val verseNum = String(Character.toChars(baseNum + (verseId-1)))
            val sharedPreferences = context.getSharedPreferences("QuranAppPreferences", MODE_PRIVATE)
            val fontSizePosition = sharedPreferences.getInt("fontSize", 4)
            val textSize = when (fontSizePosition){
                0->20F
                1-> 22F
                2-> 24F
                3-> 26F
                4-> 28F
                5-> 30F
                6-> 32F
                7-> 34F
                else->28F
            }
            verseText.textSize = textSize
            verseText.text = "${verse.text} ${verseNum}"
            verseTranslation.text = verse.translation
            itemView.focusable = FOCUSABLE

            var verseBookmarked = UserData.bookmarkList.value?.find { bookmark: Bookmark -> bookmark.surahID == surahId && bookmark.verseID == verseId  }
            Log.d("Verse bookmarked", verseBookmarked.toString())
            verseBookmark.setBackgroundResource(if (verseBookmarked != null) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border)
            verseLastRead.setBackgroundResource(if (UserData.lastRead.value != null &&
                UserData.lastRead.value?.surahID == surahId &&
                UserData.lastRead.value?.verseID ==verseId ) R.drawable.ic_flag else R.drawable.ic_outline_flag)

            verseBookmark.setOnClickListener {
                if (verseBookmarked != null) {
                    // Action when the verse is already bookmarked (remove it)
                    UserData.removeBookmark(context,Bookmark(surahId, verseId)) {isSuccess->
                        if (isSuccess){

                            verseBookmarked = null
                            verseBookmark.setBackgroundResource(R.drawable.ic_bookmark_border)
                            Log.d("Bookmark", "Removed bookmark for Surah: $surahId, Verse: $verseId")
                        }
                        else{
                            Log.d("Bookmark", "Cannot Remove Bookmark")
                            verseBookmark.setBackgroundResource(R.drawable.ic_bookmark)

                        }
                    }
                } else {
                    // Action when the verse is not bookmarked (add it)
                    UserData.addBookmark(context,Bookmark(surahId, verseId)) {isSuccess->
                        if (isSuccess){
                            verseBookmarked = Bookmark(surahId, verseId)
                            verseBookmark.setBackgroundResource(R.drawable.ic_bookmark)
                            Log.d("Bookmark", "Added bookmark for Surah: $surahId, Verse: $verseId")
                        }else{
                            verseBookmark.setBackgroundResource(R.drawable.ic_bookmark_border)
                            Log.d("Bookmark", "Cannot Add to Bookmarks")

                        }
                    }
                }
            }


            verseListen.setOnClickListener {
                val recPos = sharedPreferences.getInt("voice", 0)
                val reciterId = when (recPos){
                    0 -> "2"
                    1 -> "7"
                    2 -> "5"
                    3 -> "4"
                    4 -> "3"
                    else -> "2"
                }
                fetchVerseInSurah(reciterId, surahId, verseId, context)
            }

            verseLastRead.setOnClickListener {
                UserData.setNewLastRead(context,Bookmark(surahId, verseId)) { isSuccess ->
                    if (isSuccess){
                        verseLastRead.setBackgroundResource(R.drawable.ic_flag)
                        Toast.makeText(context, "Last Read Updated Successfully.", Toast.LENGTH_LONG).show()

                    }
                    else{
                        verseLastRead.setBackgroundResource(R.drawable.ic_outline_flag)
                        Toast.makeText(context, "Something's Wrong When Updating Last Read.", Toast.LENGTH_LONG).show()
                    }
                }
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
