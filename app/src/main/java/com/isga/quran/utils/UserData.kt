package com.isga.quran.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isga.quran.data.Bookmark
import com.isga.quran.data.Reminder
import java.util.Date

object UserData {
    private val _bookmarkList= MutableLiveData<MutableList<Bookmark>>(mutableListOf())
    val bookmarkList: LiveData<MutableList<Bookmark>> get() = _bookmarkList
    private val _lastRead = MutableLiveData<Bookmark?>()
    val lastRead: LiveData<Bookmark?> get() = _lastRead
    private val _reminders = MutableLiveData<MutableList<Reminder>>(mutableListOf())
    val reminders: LiveData<MutableList<Reminder>> get() = _reminders

    fun getUserData(context: Context) {
        val gson = Gson()
        val db = FirestoreInstance.db
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Parse_lastRead.value
                        val lastReadJson = gson.toJson(document.get("last_read"))
                        _lastRead.value = gson.fromJson(lastReadJson, Bookmark::class.java)

                        // Parse `_bookmarkList.value`
                        val bookmarksJson = gson.toJson(document.get("bookmarks"))
                        _bookmarkList.value = gson.fromJson(
                            bookmarksJson,
                            object : TypeToken<MutableList<Bookmark>>() {}.type
                        )

                        //parse_reminders.value
                        val reminderList = gson.toJson(document.get("reminders"))
                       _reminders.value = gson.fromJson(
                            reminderList,
                            object : TypeToken<MutableList<Reminder>>() {}.type
                        )
                    } else {

                        setNewUserData { }
                    }

                }
                .addOnFailureListener { err ->
                    Log.d("Error User Data", "Error when retrieving User Data")
                    Toast.makeText(context, "Error when retrieving User Data", Toast.LENGTH_SHORT)
                        .show()
                }

        }
    }

    fun setNewLastRead(context: Context, newLastRead: Bookmark, callback: (Boolean) -> Unit) {
        val db = FirestoreInstance.db
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    userDoc.update("last_read", newLastRead)
                        .addOnSuccessListener {
                           _lastRead.value = newLastRead

                            callback(true)
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                } else {
                    val data = mapOf(
                        "last_read" to newLastRead,
                        "bookmarks" to mutableListOf<Bookmark>(),
                        "reminders" to reminders.value

                    )
                    userDoc.set(data)
                        .addOnSuccessListener {
                            Log.d("update last read success", "Last read updated successfully")
                           _lastRead.value = newLastRead
                            callback(true)

                        }
                        .addOnFailureListener { err ->
                            Log.d("update last read error", err.message.toString())
                            callback(false)

                        }
                }
            }.addOnFailureListener {
                Log.d("Document error", "Document not found")
                callback(false)

            }

        } else {
            val gson = Gson()
            val sharedPreferences = context.getSharedPreferences("QuranUserData", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("last_read", gson.toJson(newLastRead))
            editor.apply()
            callback(true)
        }
    }

    fun addBookmark(context: Context, bookmark: Bookmark, callback: (Boolean) -> Unit) {
        val db = FirestoreInstance.db
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    userDoc.update("bookmarks", FieldValue.arrayUnion(bookmark))
                        .addOnSuccessListener {
                            _bookmarkList.value!!.add(bookmark)
                            Log.d("Add Bookmark", "Success")
                            callback(true)
                        }
                        .addOnFailureListener { err ->
                            Log.d("Add Bookmark", err.message.toString())
                            callback(false)
                        }
                } else {
                    val data = mapOf(
                        "last_read" to lastRead.value,
                        "bookmarks" to listOf(bookmark),
                        "reminders" to reminders.value


                    )
                    userDoc.set(data)
                        .addOnSuccessListener {
                            Log.d("New Add Bookmark", "Bookmark added successfully")
                            _bookmarkList.value!!.add(bookmark)
                            callback(true)
                        }
                        .addOnFailureListener { err ->
                            Log.d("New Add Bookmark error", err.message.toString())
                            callback(false)
                        }
                }

            }.addOnFailureListener { err ->
                Log.d("New Add Bookmark error", err.message.toString())
                callback(false)

            }

        } else {
            val gson = Gson()
            val sharedPreferences = context.getSharedPreferences("QuranUserData", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val bookmarksJson = sharedPreferences.getString("bookmarks", null)
            val type = object : TypeToken<MutableList<Bookmark>>() {}.type
            val bms: MutableList<Bookmark> = gson.fromJson(bookmarksJson, type) ?: mutableListOf()

            bms.add(bookmark)

            editor.putString("bookmarks", gson.toJson(bms))
            editor.apply()
            callback(true)
        }
    }

    fun removeBookmark(context: Context, bookmark: Bookmark, callback: (Boolean) -> Unit) {
        val db = FirestoreInstance.db
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    userDoc.update("bookmarks", FieldValue.arrayRemove(bookmark))
                        .addOnSuccessListener {
                            _bookmarkList.value!!.removeIf { bm -> bm.verseID == bookmark.verseID && bm.surahID == bookmark.surahID }
                            callback(true)
                        }
                        .addOnFailureListener { err ->
                            Log.d("Remove Bookmark", err.message.toString())
                            callback(false)
                        }
                }
            }.addOnFailureListener { err ->
                Log.d("Remove Bookmark", err.message.toString())
                callback(false)
            }


        } else {
            val gson = Gson()
            val sharedPreferences = context.getSharedPreferences("QuranUserData", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val bookmarksJson = sharedPreferences.getString("bookmarks", null)
            val type = object : TypeToken<List<Bookmark>>() {}.type
            val bms: MutableList<Bookmark> = gson.fromJson(bookmarksJson, type) ?: mutableListOf()

            bms.removeIf { it.verseID == bookmark.verseID && it.surahID == bookmark.surahID }

            editor.putString("bookmarks", gson.toJson(bms))
            editor.apply()
            callback(true)
        }
    }

    fun setReminder(context: Context, reminder: Reminder, callback: (Boolean) -> Unit) {

        val rem =_reminders.value!!.find { rmd -> rmd.reminderId == reminder.reminderId }
        if (rem == null) {
            val db = FirestoreInstance.db
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userDoc = db.collection("users").document(currentUser.uid)
                userDoc.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        userDoc.update("reminders", FieldValue.arrayUnion(reminder))
                            .addOnSuccessListener {
                                Log.d("Add reminder debug", reminder.toString())
                               _reminders.value!!.add(reminder)
                                Log.d("Add Reminder", "Success")
                                callback(true)
                            }
                            .addOnFailureListener { err ->
                                Log.d("Add Reminder", err.message.toString())
                                callback(false)
                            }
                    } else {
                        val data = mapOf(
                            "last_read" to lastRead.value,
                            "bookmarks" to _bookmarkList.value,
                            "reminders" to listOf(reminder)

                        )
                        userDoc.set(data)
                            .addOnSuccessListener {
                                Log.d("New Add Reminder", "Reminder added successfully")
                               _reminders.value!!.add(reminder)
                                callback(true)
                            }
                            .addOnFailureListener { err ->
                                Log.d("New Add Reminder error", err.message.toString())
                                callback(false)
                            }
                    }

                }.addOnFailureListener { err ->
                    Log.d("New Add Reminder error", err.message.toString())
                    callback(false)

                }

            } else {
                val gson = Gson()
                val sharedPreferences = context.getSharedPreferences("QuranUserData", MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val remindersJson = sharedPreferences.getString("reminders", null)
                val type = object : TypeToken<List<Reminder>>() {}.type

                val rms: MutableList<Reminder> =
                    gson.fromJson(remindersJson, type) ?: mutableListOf()
                rms.add(reminder)
                editor.putString("reminders", gson.toJson(rms))
                editor.apply()
                callback(true)
            }
        } else {

            callback(false)
        }
    }


    fun deleteReminder(context: Context, reminder: Reminder, callback: (Boolean) -> Unit) {
        val db = FirestoreInstance.db
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val remToDel = _reminders.value!!.find { r -> r.reminderId == reminder.reminderId }

                    userDoc.update("reminders", FieldValue.arrayRemove(remToDel))

                        .addOnSuccessListener {
                           _reminders.value!!.removeIf { r -> r.reminderId == reminder.reminderId }
                            Log.d("reminder delete success", remToDel.toString())
                            callback(true)
                        }
                        .addOnFailureListener { err ->
                            Log.d("Remove reminder", err.message.toString())
                            callback(false)
                        }
                }
            }.addOnFailureListener { err ->
                Log.d("Remove reminder", err.message.toString())
                callback(false)
            }


        } else {
            val gson = Gson()
            val sharedPreferences = context.getSharedPreferences("QuranUserData", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val remindersJson = sharedPreferences.getString("reminders", null)
            val type = object : TypeToken<List<Reminder>>() {}.type

            val rms: MutableList<Reminder> = gson.fromJson(remindersJson, type) ?: mutableListOf()

            rms.removeIf { it.reminderId == reminder.reminderId }

            editor.putString("reminders", gson.toJson(rms))
            editor.apply()
            callback(true)


        }

    }

    fun updateReminder(context: Context, reminder: Reminder, callback: (Boolean) -> Unit) {
        val db = FirestoreInstance.db
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val remToDel = _reminders.value!!.find { r -> r.reminderId == reminder.reminderId }

                    userDoc.update("reminders", FieldValue.arrayRemove(remToDel))

                        .addOnSuccessListener {
                            _reminders.value!!.removeIf { r -> r.reminderId == reminder.reminderId }
                            userDoc.update("reminders", FieldValue.arrayUnion(reminder)).addOnSuccessListener {
                                callback(true)
                            }.addOnFailureListener {err->
                                Log.d("Update reminder", err.message.toString())
                                callback(false)

                            }
                        }
                        .addOnFailureListener { err ->
                            Log.d("Update reminder", err.message.toString())
                            callback(false)
                        }
                }
            }.addOnFailureListener { err ->
                Log.d("Update reminder", err.message.toString())
                callback(false)
            }


        }
    }

    fun setNewUserData(callback: (Boolean) -> Unit) {
        val db = FirestoreInstance.db
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userDoc = db.collection("users").document(currentUser.uid)
            userDoc.get().addOnSuccessListener { document ->
                val data = mapOf(
                    "last_read" to lastRead.value,
                    "bookmarks" to _bookmarkList.value,
                    "reminders" to reminders.value,

                    )
                userDoc.set(data)
                    .addOnSuccessListener {
                        Log.d("set new user successful", "Set data for new user Success.")
                        callback(true)

                    }
                    .addOnFailureListener { err ->
                        Log.d("set new user error", err.message.toString())
                        callback(false)

                    }
            }
        }
    }
}
fun debug() {
    Log.d("calendar", Calendar.getInstance().time.toString())
}