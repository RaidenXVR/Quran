package com.isga.quran.adapter

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isga.quran.R
import com.isga.quran.data.Reminder
import com.isga.quran.utils.UserData
import com.isga.quran.utils.UserData.reminders
import com.isga.quran.utils.UserData.setReminder
import com.isga.quran.utils.cancelReminder
import com.isga.quran.utils.updateReminder
import okhttp3.internal.notify

class ReminderAdapter(private val adapterReminders: MutableList<Reminder>) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(view: View, private val adapterReminders: MutableList<Reminder>) : RecyclerView.ViewHolder(view) {
        private val reminderName = view.findViewById<TextView>(R.id.reminder_name)
        private val reminderTime = view.findViewById<TextView>(R.id.reminder_time)
        private val reminderEdit = view.findViewById<RelativeLayout>(R.id.reminder_edit)
        private val reminderDelete = view.findViewById<RelativeLayout>(R.id.reminder_delete)

        fun bind(reminder: Reminder) {
            reminderName.text = reminder.name
            reminderTime.text =
                itemView.context.getString(
                    R.string.time_format_24,
                    reminder.hour.toString().padStart(2, '0'),
                    reminder.minute.toString().padStart(2, '0')
                )
            reminderEdit.setOnClickListener { view ->
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val thisReminder =
                    reminders.value?.find { rem -> rem.reminderId == reminder.reminderId }
                if (thisReminder == null) {
                    return@setOnClickListener
                }
                TimePickerDialog(view.context, { _, selectedHour, selectedMinute ->
                    val context = view.context
                    updateReminder(
                        context,
                        thisReminder.reminderId,
                        selectedHour,
                        selectedMinute,
                        thisReminder.name
                    )
                    reminderTime.text =
                        itemView.context.getString(
                            R.string.time_format_24,
                            selectedHour.toString().padStart(2, '0'),
                            selectedMinute.toString().padStart(2, '0')
                        )
                    val toChange = adapterReminders.find { r -> r.reminderId == reminder.reminderId }
                    toChange!!.hour = selectedHour
                    toChange.minute = selectedMinute
                    bindingAdapter?.notifyDataSetChanged()
                    Log.d("reminderTime", adapterReminders.toString())

                }, hour, minute, true).show()
            }

            reminderDelete.setOnClickListener {
                cancelReminder(it.context, reminder.reminderId)
                adapterReminders.remove(reminder)
                bindingAdapter?.notifyDataSetChanged()
                Log.d("reminders after delete", reminders.value!!.toString())

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view, adapterReminders)
    }

    override fun getItemCount(): Int {
        return adapterReminders.size
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = adapterReminders[position]
        holder.bind(reminder)
    }
}