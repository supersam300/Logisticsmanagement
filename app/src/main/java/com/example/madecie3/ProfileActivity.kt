package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val nameText    = findViewById<TextView>(R.id.profileName)
        val emailText   = findViewById<TextView>(R.id.profileEmail)
        val logoutBtn   = findViewById<Button>(R.id.logoutBtn)
        val auth        = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }

        nameText.text  = currentUser.displayName ?: "User"
        emailText.text = currentUser.email ?: "No email"

        // SharedPreferences Use #2: Cache Firebase profile data locally
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("cached_name",  currentUser.displayName ?: "User")
            .putString("cached_email", currentUser.email ?: "No email")
            .apply()

        logoutBtn.setOnClickListener {
            // SharedPreferences Use #2: Clear local cache on logout
            getSharedPreferences("prefs", MODE_PRIVATE).edit().clear().apply()
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }
}