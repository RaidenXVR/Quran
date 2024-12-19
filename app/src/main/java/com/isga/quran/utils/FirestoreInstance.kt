package com.isga.quran.utils

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreInstance {
    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}