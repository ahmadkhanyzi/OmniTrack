package com.example.omnitrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val inputField = findViewById<EditText>(R.id.signup_email_phone)
        val passwordField = findViewById<EditText>(R.id.signup_password)
        val confirmPasswordField = findViewById<EditText>(R.id.signup_confirm_password)
        val signupButton = findViewById<Button>(R.id.signup_button)
        val signInButton = findViewById<Button>(R.id.signin_button)

        signupButton.setOnClickListener {
            val input = inputField.text.toString().trim()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (!isValidInput(input, password, confirmPassword)) return@setOnClickListener

            saveTempUserData(input)

            if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                sendEmailOtp(input)
            } else {
                sendPhoneOtp(input)
            }
        }

        signInButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun isValidInput(identifier: String, password: String, confirmPassword: String): Boolean {
        return when {
            identifier.isEmpty() -> {
                Toast.makeText(this, "Enter Email or Phone Number", Toast.LENGTH_SHORT).show()
                false
            }
            Patterns.EMAIL_ADDRESS.matcher(identifier).matches() -> {
                if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    false
                } else if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    false
                } else {
                    true
                }
            }
            Patterns.PHONE.matcher(identifier).matches() -> true
            else -> {
                Toast.makeText(this, "Enter a valid Email or Phone Number", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    private fun saveTempUserData(identifier: String) {
        val database = FirebaseDatabase.getInstance("https://omni-track-ae57d-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        val userId = database.push().key
        val user = User(identifier)
        userId?.let {
            database.child("TempUsers").child(it).setValue(user)
        }
    }

    private fun sendEmailOtp(email: String) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://omni-track-ae57d.firebaseapp.com/verify?email=$email") // ✅ Use Firebase's default domain
            .setHandleCodeInApp(true)
            .setAndroidPackageName(packageName, true, null)
            .build()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email OTP sent!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, OtpVerificationActivity::class.java)
                    intent.putExtra("identifier", email)
                    intent.putExtra("verificationType", "email")
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to send OTP: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendPhoneOtp(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserToDatabase(phone)
                            Toast.makeText(this@SignupActivity, "Auto Verified!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                        } else {
                            Toast.makeText(this@SignupActivity, "Verification Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@SignupActivity, "OTP Error: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    val intent = Intent(this@SignupActivity, OtpVerificationActivity::class.java)
                    intent.putExtra("identifier", phone)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("verificationType", "phone")
                    startActivity(intent)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun saveUserToDatabase(identifier: String) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance("https://omni-track-ae57d-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        val user = User(identifier)

        database.child("Users").child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

data class User(val identifier: String)
