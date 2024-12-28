package com.isga.quran.custom

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.content.ContextCompat.getString
import com.isga.quran.R
import com.isga.quran.utils.rescheduleReminder
import com.isga.quran.utils.scheduleReminder

class ReminderNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val remId = intent.getIntExtra("reminder_id", 0)
        val name = intent.getStringExtra("name")
        val hour = intent.getIntExtra("hour", 9)
        val minute = intent.getIntExtra("minute", 0)
        if (remId == 0){
            Log.d("remid 0", "$remId, $name, $hour, $minute")
            return
        }
        val notification = NotificationCompat.Builder(context, "reminder_1")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(name)
            .setContentText(getString(context, R.string.reminder_message))
            .setPriority(PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(remId, notification)
        Log.d("reminder notif", "$remId, $name, $hour, $minute, $context")
        // reschedule
        rescheduleReminder(remId, name!!, hour, minute, context)
    }
}


