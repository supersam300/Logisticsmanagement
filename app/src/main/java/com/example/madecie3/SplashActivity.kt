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

        Handler(Looper.getMainLooper()).postDelayed({
            val nextScreen = if (FirebaseAuth.getInstance().currentUser != null) {
                DashboardActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this, nextScreen))
            finish()
        }, 2000)
    }
}