package com.isga.quran.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.isga.quran.R
import com.isga.quran.SignInActivity
import com.isga.quran.data.Reminder
import com.isga.quran.utils.UserData
import com.isga.quran.utils.cancelReminder
import com.isga.quran.utils.createNotificationChannelFun
import com.isga.quran.utils.isReminderSet
import com.isga.quran.utils.parseSurah
import com.isga.quran.utils.scheduleAllReminders
import kotlin.math.log

class SettingsFragment: Fragment() {
    private lateinit var radioGroupMode: RadioGroup
    private lateinit var spinnerLanguage: Spinner
    private lateinit var spinnerVoice: Spinner
    private lateinit var spinnerFontSize: Spinner
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var parentView: View
    private lateinit var logoutButton: Button
    private lateinit var debugButt: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize views
        radioGroupMode = view.findViewById(R.id.radioGroupMode)
        spinnerLanguage =view.findViewById(R.id.spinnerLanguage)
        spinnerVoice =view.findViewById(R.id.spinnerVoice)
        spinnerFontSize =view.findViewById(R.id.spinnerFontSize)
        logoutButton = view.findViewById(R.id.logout_button)

        debugButt = view.findViewById(R.id.debug_butt)

        debugButt.setOnClickListener {
            for (rem in UserData.reminders.value!!){
                Log.d("reminders active", isReminderSet(requireContext(), rem.reminderId).toString())
            }
        }

        parentView = view

        // Set up SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("QuranAppPreferences", MODE_PRIVATE)

        //logout
        logoutButton.setOnClickListener {
            val pref = requireContext().getSharedPreferences("QuranUserData", MODE_PRIVATE)
            val edit = pref.edit()
            val noAccount = pref.getBoolean("noAccount", false)
            edit.putBoolean("noAccount", false)
            edit.putString("bookmarks", null)
            edit.putString("reminders", null)
            edit.putString("last_read", null)
            for (rem in UserData.reminders.value!!){
                cancelReminder(requireContext(), rem.reminderId)
            }
            edit.apply()
            if (!noAccount) {
                val auth = FirebaseAuth.getInstance()
                auth.signOut()
            }
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // Set up Spinner for Language
        val languageAdapter = ArrayAdapter.createFromResource(
            view.context,
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = languageAdapter

        // Set up Spinner for Voice
        val voiceAdapter = ArrayAdapter.createFromResource(
            view.context,
            R.array.voice_options,
            android.R.layout.simple_spinner_item
        )
        voiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVoice.adapter = voiceAdapter

        // Set up Spinner for Font Size
        val fontSizeAdapter = ArrayAdapter.createFromResource(
            view.context,
            R.array.font_sizes,
            android.R.layout.simple_spinner_item
        )
        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFontSize.adapter = fontSizeAdapter

        // Load saved preferences
        loadPreferences()

        // Handle saving preferences when changed
        radioGroupMode.setOnCheckedChangeListener { _, _ -> savePreferences() }

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                savePreferences()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        spinnerVoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                savePreferences()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        spinnerFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                savePreferences()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }
    private fun loadPreferences() {
        // Load mode (Light/Dark)
        val isDarkMode = sharedPreferences.getBoolean("mode", false)
        val selectedMode = if (isDarkMode) R.id.radioDark else R.id.radioLight
        parentView.findViewById<RadioButton>(selectedMode).isChecked = true
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)

        // Load language
        val languagePosition = sharedPreferences.getInt("language", 0)
        spinnerLanguage.setSelection(languagePosition)

        // Load voice
        val voicePosition = sharedPreferences.getInt("voice", 0)
        spinnerVoice.setSelection(voicePosition)

        // Load font size
        val fontSizePosition = sharedPreferences.getInt("fontSize", 0)
        spinnerFontSize.setSelection(fontSizePosition)
    }

    private fun savePreferences() {
        val editor = sharedPreferences.edit()

        // Save mode
        val isDarkMode = radioGroupMode.checkedRadioButtonId == R.id.radioDark
        editor.putBoolean("mode", isDarkMode)
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)

        // Save language
        val languagePosition = spinnerLanguage.selectedItemPosition
        editor.putInt("language", languagePosition)

        // Save voice
        val voicePosition = spinnerVoice.selectedItemPosition
        editor.putInt("voice", voicePosition)

        // Save font size
        val fontSizePosition = spinnerFontSize.selectedItemPosition
        editor.putInt("fontSize", fontSizePosition)


        parseSurah(requireContext())
        editor.apply()
    }

}