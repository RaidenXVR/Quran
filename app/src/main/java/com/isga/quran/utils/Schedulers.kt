package com.isga.quran.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.isga.quran.R
import com.isga.quran.custom.ReminderNotification
import com.isga.quran.data.Reminder
import com.isga.quran.utils.UserData.deleteReminder
import com.isga.quran.utils.UserData.reminders
import java.util.Calendar
import java.util.Date


fun scheduleAllReminders(reminders: List<Reminder>, context: Context) {
    for (reminder in reminders) {
        scheduleReminder(
            reminder.reminderId,
            reminder.name,
            reminder.hour,
            reminder.minute,
            context
        )
    }
}

//@RequiresApi(Build.VERSION_CODES.S)
fun scheduleReminder(id: Int, name: String, hour: Int, minute: Int, context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderNotification::class.java).apply {
        putExtra("reminder_id", id) // Pass the reminder ID if needed
        putExtra("name", name) // Pass the name
        putExtra("hour", hour) // Pass hour for rescheduling
        putExtra("minute", minute) // Pass minute for rescheduling
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, id, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // Ensure the alarm is scheduled for the next occurrence
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    // Check if exact alarms can be scheduled (API 31+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            // Show warning to the user or handle the situation
            Log.w("AlarmManager", "Exact alarms cannot be scheduled on this device.")
        }
    }
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

fun rescheduleReminder(id: Int, name:String, hour: Int, minute: Int, context: Context){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderNotification::class.java).apply {
        putExtra("reminder_id", id) // Pass the reminder ID if needed
        putExtra("name", name) // Pass the name
        putExtra("hour", hour) // Pass hour for rescheduling
        putExtra("minute", minute) // Pass minute for rescheduling
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, id, intent,  PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }
    Log.d("calendar time", calendar.timeInMillis.toString())

    // Ensure the alarm is scheduled for the next occurrence
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    Log.d("calendar time", calendar.timeInMillis.toString())

    // Check if exact alarms can be scheduled (API 31+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            // Show warning to the user or handle the situation
            Log.w("AlarmManager", "Exact alarms cannot be scheduled on this device.")
        }
    }
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

fun createNotificationChannelFun(context: Context) {
    val name = "Reminder Notification Channel"
    val desc = "Channel for Reminder of the Qur'an Q App"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("reminder_1", name, importance)
    channel.description = desc

    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun cancelReminder(context: Context, reminderId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderNotification::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminderId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val reminder = reminders.value!!.find { rm -> rm.reminderId == reminderId }
    if (reminder != null) {
        deleteReminder(context, reminder) {}
    }

    alarmManager.cancel(pendingIntent)
    Toast.makeText(context, R.string.reminder_cancel_success, Toast.LENGTH_LONG).show()
}

fun updateReminder(context: Context, reminderId: Int, hour: Int, minute: Int, name: String) {

    UserData.updateReminder(context, Reminder(name, reminderId, hour, minute)) {
        if (it) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Cancel the existing alarm
            val cancelIntent = Intent(context, ReminderNotification::class.java)
            val cancelPendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(cancelPendingIntent)

            // Create a new Intent with updated data
            val updatedIntent = Intent(context, ReminderNotification::class.java).apply {
                putExtra("reminder_id", reminderId) // Pass the updated reminder ID
                putExtra("name", name) // Pass the updated name
                putExtra("hour", hour) // Pass the updated hour
                putExtra("minute", minute) // Pass the updated minute
            }
            val updatedPendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                updatedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule the updated alarm
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1) // Ensure the alarm is set for the next day
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.w("AlarmManager", "Exact alarms cannot be scheduled on this device.")
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                updatedPendingIntent
            )
            Log.d("Reminder Update", "$reminderId $hour $minute")

            Toast.makeText(context, R.string.reminder_update_success, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, R.string.general_data_error, Toast.LENGTH_LONG).show()
        }
    }
}

fun isReminderSet(context: Context, reminderId: Int): Boolean {
    val intent = Intent(context, ReminderNotification::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminderId,
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    return pendingIntent != null
}
