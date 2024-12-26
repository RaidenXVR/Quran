package com.isga.quran.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isga.quran.data.Bookmark


var bookmarkList: List<Bookmark> = listOf()
var lastRead: Bookmark? = null

fun getUserData(context: Context){
    val gson = Gson()
    val db = FirestoreInstance.db
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.let {
        db.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()){

                    // Parse lastRead
                    val lastReadJson = gson.toJson(document.get("last_read"))
                    lastRead = gson.fromJson(lastReadJson, Bookmark::class.java)

                    // Parse `bookmarkList`
                    val bookmarksJson = gson.toJson(document.get("bookmarks"))
                    bookmarkList = gson.fromJson(bookmarksJson, object : TypeToken<List<Bookmark>>() {}.type)
                }

            }
            .addOnFailureListener { err ->
                Log.d("Error Bookmark", "Error when retrieving bookmarks")
                Toast.makeText(context, err.message, Toast.LENGTH_SHORT).show()
            }

    }
}

fun setNewLastRead(newLastRead: Bookmark, callback:(Boolean)-> Unit) {
    val db = FirestoreInstance.db
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.let {
        val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
            if (document.exists()){
                userDoc.update("last_read", newLastRead)
                    .addOnSuccessListener {
                        lastRead = newLastRead

                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }else   {
                val data = mapOf(
                    "last_read" to newLastRead,
                    "bookmarks" to listOf<Bookmark>()

                )
                userDoc.set(data)
                    .addOnSuccessListener {
                        Log.d("update last read success", "Last read updated successfully")
                        lastRead = newLastRead
                        callback(true)

                    }
                    .addOnFailureListener { err ->
                        Log.d("update last read error", err.message.toString())
                        callback(false )

                    }
            }
            }.addOnFailureListener {
                Log.d("Document error", "Document not found")
                callback(false)

            }

    }?: run {
        Log.d("User error", "User not found")
        callback(false)

    }
}

fun addBookmark(bookmark: Bookmark, callback: (Boolean) -> Unit){
    val db = FirestoreInstance.db
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.let {
        val userDoc = db.collection("users").document(currentUser.uid)
        userDoc.get().addOnSuccessListener { document ->
            if (document.exists()){
                userDoc.update("bookmarks", FieldValue.arrayUnion(bookmark))
                    .addOnSuccessListener {
                        bookmarkList = bookmarkList + listOf(bookmark)
                        Log.d("Add Bookmark", "Success")
                        callback(true)
                    }
                    .addOnFailureListener { err ->
                        Log.d("Add Bookmark", err.message.toString())
                        callback(false)
                    }
            }
            else{
                val data = mapOf(
                    "last_read" to lastRead,
                    "bookmarks" to listOf(bookmark)

                )
                userDoc.set(data)
                    .addOnSuccessListener {
                        Log.d("New Add Bookmark", "Bookmark added successfully")
                        bookmarkList = bookmarkList + listOf(bookmark)
                       callback(true)
                    }
                    .addOnFailureListener { err ->
                        Log.d("New Add Bookmark error", err.message.toString())
                        callback(false)
                    }
            }

        }.addOnFailureListener { err->
            Log.d("New Add Bookmark error", err.message.toString())
            callback(false)

        }

    }?.addOnFailureListener { err ->
        Log.d("Add Bookmark", err.message.toString())
        callback(false)
    }
}

fun removeBookmark(bookmark: Bookmark, callback: (Boolean) -> Unit){
    val db = FirestoreInstance.db
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.let {
        val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    userDoc.update("bookmarks", FieldValue.arrayRemove(bookmark))
                        .addOnSuccessListener {
                            bookmarkList =
                                bookmarkList.filter { bm: Bookmark -> bm.verseID != bookmark.verseID && bm.surahID != bookmark.verseID }
                            callback(true)
                        }
                        .addOnFailureListener {err->
                            Log.d("Remove Bookmark", err.message.toString())
                            callback(false)
                        }
                }
            }.addOnFailureListener {err->
                Log.d("Remove Bookmark", err.message.toString())
                callback(false)
            }


    }?.addOnFailureListener {err->
        Log.d("Remove Bookmark", err.message.toString())
        callback(false)
    }
}