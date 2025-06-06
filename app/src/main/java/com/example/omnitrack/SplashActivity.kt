package com.example.omnitrack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure SplashScreen API runs correctly
        installSplashScreen()

        setContentView(R.layout.activity_splash)

        // Get UI elements
        val appName = findViewById<TextView>(R.id.app_name)
        val tagline = findViewById<TextView>(R.id.tagline)
        val logo = findViewById<ImageView>(R.id.logo)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Apply animations
        logo.startAnimation(bounce)
        appName.startAnimation(slideUp)
        tagline.startAnimation(slideUp)

        // Ensure fade-in effect follows motion
        logo.postDelayed({ logo.startAnimation(fadeIn) }, 800)
        appName.postDelayed({ appName.startAnimation(fadeIn) }, 800)
        tagline.postDelayed({ tagline.startAnimation(fadeIn) }, 800)

        // Redirect to LoginActivity after splash screen duration
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Ensures SplashActivity is removed from the back stack
        }, 2500) // Waits for 2.5 seconds before switching
    }
}
