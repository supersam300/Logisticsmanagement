package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.CartProduct
import com.example.madecie3.api.PaymentRequest
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val amount          = intent.getIntExtra("amount", 0)
        val amountText      = findViewById<TextView>(R.id.amountText)
        val payBtn          = findViewById<Button>(R.id.payBtn)
        val progressBar     = findViewById<ProgressBar>(R.id.paymentProgress)
        val statusText      = findViewById<TextView>(R.id.paymentStatusText)
        val paymentGroup    = findViewById<RadioGroup>(R.id.paymentGroup)

        amountText.text = "Amount: ₹$amount"

        payBtn.setOnClickListener {

            // Validate: user must select a payment method first
            if (paymentGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedMethod = findViewById<RadioButton>(paymentGroup.checkedRadioButtonId).text.toString()

            // Show loading state
            progressBar.visibility = View.VISIBLE
            statusText.visibility  = View.VISIBLE
            statusText.text        = "Processing payment via $selectedMethod..."
            payBtn.isEnabled       = false

            // Build mock payment request body
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val paymentRequest = PaymentRequest(
                userId   = 1,
                date     = today,
                products = listOf(CartProduct(productId = 1, quantity = 1))
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.simulatePayment(paymentRequest)

                    if (response.isSuccessful && response.body() != null) {
                        // Build a unique tracking ID from the API response
                        val transactionId = "TRK${response.body()!!.id}${(1000..9999).random()}"

                        val intent = Intent(this@PaymentActivity, OrderConfirmationActivity::class.java)
                        intent.putExtra("trackingId", transactionId)
                        intent.putExtra("paymentMethod", selectedMethod)
                        intent.putExtra("amount", amount)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@PaymentActivity, "Payment failed. Please try again.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@PaymentActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    progressBar.visibility = View.GONE
                    statusText.visibility  = View.GONE
                    payBtn.isEnabled       = true
                }
            }
        }
    }
}