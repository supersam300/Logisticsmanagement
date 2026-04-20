package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val trackingId        = intent.getStringExtra("trackingId") ?: "N/A"
        val paymentMethod     = intent.getStringExtra("paymentMethod") ?: "Unknown"
        val amount            = intent.getIntExtra("amount", 0)

        val trackingText      = findViewById<TextView>(R.id.trackingText)
        val paymentMethodText = findViewById<TextView>(R.id.paymentMethodText)
        val amountPaidText    = findViewById<TextView>(R.id.amountPaidText)
        val homeBtn           = findViewById<Button>(R.id.homeBtn)

        trackingText.text      = "Tracking ID: $trackingId"
        paymentMethodText.text = "Payment Method: $paymentMethod"
        amountPaidText.text    = "Amount Paid: ₹$amount"

        homeBtn.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}