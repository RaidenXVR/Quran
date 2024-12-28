package com.isga.quran.custom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.isga.quran.R

class ReminderDialog(context: Context): Dialog(context) {
    lateinit var cancelButt: Button
    lateinit var confirmButt: Button
    lateinit var setTime: Button
    lateinit var timeText: TextView
    lateinit var reminderName: EditText
    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_set_reminder)

        cancelButt = findViewById(R.id.cancel_reminder)
        confirmButt = findViewById(R.id.confirm_reminder)
        setTime = findViewById(R.id.set_time_button)
        timeText = findViewById(R.id.selected_time)
        reminderName = findViewById(R.id.set_reminder_name)
    }
}