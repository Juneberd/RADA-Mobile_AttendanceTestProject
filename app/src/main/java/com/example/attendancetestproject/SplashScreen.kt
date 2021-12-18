package com.example.attendancetestproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val mAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_anim)
        val bAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        imageViewSplash.startAnimation(bAnimation)
        textViewSplash.startAnimation(mAnimation)

        val splashScreenTimeOut = 4000
        val homeIntent = Intent(this, MainActivity::class.java)

        Handler().postDelayed({
            startActivity(homeIntent)
            finish()
        }, splashScreenTimeOut.toLong())
    }
}