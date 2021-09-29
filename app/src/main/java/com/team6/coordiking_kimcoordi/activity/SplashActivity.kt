package com.team6.coordiking_kimcoordi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R

class SplashActivity : AppCompatActivity() {

    private var user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            if(user==null) startActivity(Intent(this, SignInActivity::class.java))
            else startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}