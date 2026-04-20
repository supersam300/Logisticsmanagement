package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // SharedPreferences Use #1: Track how many times the app has been opened
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val launchCount = prefs.getInt("app_launch_count", 0) + 1
        prefs.edit().putInt("app_launch_count", launchCount).apply()

        Handler(Looper.getMainLooper()).postDelayed({

            // Firebase still decides which screen to route to
            val nextScreen = if (FirebaseAuth.getInstance().currentUser != null) {
                DashboardActivity::class.java
            } else {
                LoginActivity::class.java
            }

            // SharedPreferences Use #1: Save the last screen the user was taken to
            prefs.edit()
                .putString("last_destination", nextScreen.simpleName)
                .apply()

            startActivity(Intent(this, nextScreen))
            finish()

        }, 2000)
    }
}
