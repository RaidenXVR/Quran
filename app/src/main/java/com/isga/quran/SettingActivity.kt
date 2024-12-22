package com.isga.quran

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingActivity : AppCompatActivity() {

    private lateinit var radioGroupMode: RadioGroup
    private lateinit var spinnerLanguage: Spinner
    private lateinit var spinnerVoice: Spinner
    private lateinit var spinnerFontSize: Spinner
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        // Initialize views
        radioGroupMode = findViewById(R.id.radioGroupMode)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        spinnerVoice = findViewById(R.id.spinnerVoice)
        spinnerFontSize = findViewById(R.id.spinnerFontSize)

        // Set up SharedPreferences
        sharedPreferences = getSharedPreferences("QuranAppPreferences", MODE_PRIVATE)

        // Set up Spinner for Language
        val languageAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = languageAdapter

        // Set up Spinner for Voice
        val voiceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.voice_options,
            android.R.layout.simple_spinner_item
        )
        voiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVoice.adapter = voiceAdapter

        // Set up Spinner for Font Size
        val fontSizeAdapter = ArrayAdapter.createFromResource(
            this,
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadPreferences() {
        // Load mode (Light/Dark)
        val isDarkMode = sharedPreferences.getBoolean("mode", false)
        val selectedMode = if (isDarkMode) R.id.radioDark else R.id.radioLight
        findViewById<RadioButton>(selectedMode).isChecked = true

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

        // Save language
        val languagePosition = spinnerLanguage.selectedItemPosition
        editor.putInt("language", languagePosition)

        // Save voice
        val voicePosition = spinnerVoice.selectedItemPosition
        editor.putInt("voice", voicePosition)

        // Save font size
        val fontSizePosition = spinnerFontSize.selectedItemPosition
        editor.putInt("fontSize", fontSizePosition)

        editor.apply()
    }
}
