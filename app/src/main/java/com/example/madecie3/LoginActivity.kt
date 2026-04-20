package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var googleLoginBtn: Button
    private lateinit var signupText: TextView
    private lateinit var progressBar: ProgressBar

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                setLoading(false)
                return@registerForActivityResult
            }

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrEmpty()) {
                    setLoading(false)
                    Toast.makeText(this, "Google sign-in token missing", Toast.LENGTH_LONG).show()
                    return@registerForActivityResult
                }
                firebaseAuthWithGoogle(idToken)
            } catch (e: ApiException) {
                setLoading(false)
                Toast.makeText(this, "Google sign-in failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        googleLoginBtn = findViewById(R.id.googleLoginBtn)
        signupText = findViewById(R.id.signupText)
        progressBar = findViewById(R.id.loginProgress)

        val webClientIdResId = resources.getIdentifier("default_web_client_id", "string", packageName)
        if (webClientIdResId == 0) {
            googleLoginBtn.isEnabled = false
            Toast.makeText(
                this,
                "Google Sign-In not configured. Check google-services setup.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(webClientIdResId))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
        }

        loginBtn.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText  = password.text.toString().trim()

            if (emailText.isEmpty()) { email.error = "Enter email"; return@setOnClickListener }
            if (passText.isEmpty())  { password.error = "Enter password"; return@setOnClickListener }

            setLoading(true)

            auth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this) { task ->
                    setLoading(false)

                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Invalid credentials"
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        googleLoginBtn.setOnClickListener {
            if (!::googleSignInClient.isInitialized) {
                Toast.makeText(this, "Google Sign-In not configured", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            setLoading(true)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Google authentication failed"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        loginBtn.isEnabled = !isLoading
        googleLoginBtn.isEnabled = !isLoading
        signupText.isEnabled = !isLoading
    }
}