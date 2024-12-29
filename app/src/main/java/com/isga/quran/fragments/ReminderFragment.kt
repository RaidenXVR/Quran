package com.isga.quran.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.isga.quran.R
import com.isga.quran.VerseActivity
import com.isga.quran.adapter.BookmarkAdapter
import com.isga.quran.adapter.ReminderAdapter
import com.isga.quran.custom.ReminderDialog
import com.isga.quran.data.Reminder
import com.isga.quran.utils.UserData.reminders
import com.isga.quran.utils.UserData.setReminder
import com.isga.quran.utils.createNotificationChannelFun
import com.isga.quran.utils.isReminderSet

import com.isga.quran.utils.scheduleReminder

class ReminderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.reminder_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addReminderButt = view.findViewById<Button>(R.id.add_reminder)

        addReminderButt.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val dialog = ReminderDialog(requireContext())

            var remHour: Int? = null
            var remMinute: Int? = null
            dialog.show()


            dialog.setTime.setOnClickListener {
                TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                    dialog.timeText.text =
                        getString(R.string.time_format_24, selectedHour.toString().padStart(2,'0')
                            , selectedMinute.toString().padStart(2,'0'))
                    remHour = selectedHour
                    remMinute = selectedMinute

                }, hour, minute, true).show()

            }

            dialog.cancelButt.setOnClickListener {
                dialog.dismiss()
            }

            dialog.confirmButt.setOnClickListener {
                if (remHour != null && remMinute != null && dialog.reminderName.text.toString() != "") {
                    val inReminders = reminders.value?.find { reminder: Reminder ->
                        reminder.reminderId == "${remHour}${remMinute}".toInt()
                                || reminder.name == dialog.reminderName.text.toString()
                    }
                    if (inReminders == null) {
                        val rem = Reminder(
                            dialog.reminderName.text.toString(), "${remHour}${remMinute}".toInt(),
                            remHour!!, remMinute!!
                        )
                        setReminder(requireContext(),rem) {
                            if (it) {

                                scheduleReminder(rem.reminderId, rem.name, rem.hour, rem.minute, requireContext())

                                dialog.dismiss()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, ReminderFragment())
                                    .commit()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.general_data_error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.reminder_exist_title))
                            .setMessage(getString(R.string.reminder_exist_message)).show()
                    }
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.reminder_input_empty_title))
                        .setMessage(getString(R.string.reminder_input_empty_message)).show()
                }
            }


        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requireActivity().requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
            }
        } else{
            for (rem in reminders.value!!){
                if (!isReminderSet(requireContext(), rem.reminderId)){
                    scheduleReminder(rem.reminderId, rem.name, rem.hour, rem.minute, requireContext())
                }
            }
        }
        createNotificationChannelFun(requireContext())
        val adapter = ReminderAdapter(reminders.value!!)
        val recyclerView = view.findViewById<RecyclerView>(R.id.reminderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        reminders.observe(viewLifecycleOwner) { reminder ->
//            adapter.submitList(bookmarkList)
            adapter.notifyDataSetChanged() // Ensure UI is refreshed
        }

    }


}