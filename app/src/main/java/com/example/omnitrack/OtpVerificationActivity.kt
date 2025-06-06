package com.example.omnitrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase

class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var identifier: String? = null
    private var verificationType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        auth = FirebaseAuth.getInstance()
        verificationId = intent.getStringExtra("verificationId")
        identifier = intent.getStringExtra("identifier")
        verificationType = intent.getStringExtra("verificationType")

        val otpField = findViewById<EditText>(R.id.otp_input)
        val verifyButton = findViewById<Button>(R.id.verify_otp_button)

        verifyButton.setOnClickListener {
            val otp = otpField.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Enter a valid OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (verificationType == "phone") {
                verifyPhoneOtp(otp)
            } else {
                verifyEmailOtp(otp)
            }
        }
    }

    private fun verifyPhoneOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completeUserAuthentication()
            } else {
                Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyEmailOtp(otp: String) {
        if (!auth.isSignInWithEmailLink(otp)) {
            Toast.makeText(this, "Invalid Email Link. Try again.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailLink(identifier!!, otp).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completeUserAuthentication()
            } else {
                Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun completeUserAuthentication() {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance("https://omni-track-ae57d-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        // Check if the user is already registered
        database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "User Logged In Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show()
                database.child("Users").child(userId).setValue(User(identifier!!))
            }
            redirectToHome()
        }
    }

    private fun redirectToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
