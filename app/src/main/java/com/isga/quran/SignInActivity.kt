package com.isga.quran

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.isga.quran.data.Bookmark
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()


        val currentUser = auth.currentUser
        val pref = getSharedPreferences("QuranUserData", MODE_PRIVATE)
        val pref2 = getSharedPreferences("QuranAppPreferences", MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        if (!pref2.contains("mode")) {
            val userTheme: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                userTheme = resources.configuration.isNightModeActive
            } else {
                userTheme = resources.configuration.uiMode == Configuration.UI_MODE_NIGHT_YES
            }


            if (userTheme) {
                pref2.edit().putBoolean("mode", true).apply()
            } else {
                pref2.edit().putBoolean("mode", false).apply()
            }
        }
        else{
            val isDarkMode = pref2.getBoolean("mode", false)
            AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)


        }

        if (currentUser != null || pref.getBoolean("noAccount", false)) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }


        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            signIn()
        }
        val noSignInButton: Button = findViewById(R.id.no_sign_in_button)
        noSignInButton.setOnClickListener {
            val edit = pref.edit()

            edit.putBoolean("noAccount", true)
            edit.apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

    private fun signIn() {

        val credentialManager = CredentialManager.create(this)
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.WEB_CLIENT_ID))
            .setAutoSelectEnabled(true)
            .setNonce("nnmeinns")
            .build()
        val req = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result =
                    credentialManager.getCredential(this@SignInActivity, request = req)
                //get the result here
                onSignInResponse(result.credential)
            } catch (e: GetCredentialException){
                Log.d("Error get Credential", e.message.toString())
            }
        }
    }
    private fun onSignInResponse(credential: Credential){
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        }
        else {
            Log.e("Error credential", "Unexpected credential")
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    val pref = getSharedPreferences("QuranUserData", MODE_PRIVATE)
                    val edit = pref.edit()
                    edit.putBoolean("noAccount", false)
                    edit.apply()
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }

    }
}