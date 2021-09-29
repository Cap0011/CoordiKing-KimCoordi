package com.team6.coordiking_kimcoordi.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_btn_wardrobe.setOnClickListener {
            startActivity(Intent(this, MyWardrobeActivity::class.java))
        }
        main_btn_outfit.setOnClickListener {
            startActivity(Intent(this, MyOutfitsActivity::class.java))
        }
        main_btn_camera.setOnClickListener {
            startActivity(Intent(this, SnapActivity::class.java))
        }
        main_btn_mypage.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
        main_btn_favourite.setOnClickListener {
            startActivity(Intent(this, MyWardrobeActivity::class.java))
        }
        main_btn_simulator.setOnClickListener {
            startActivity(Intent(this, SimulatorActivity::class.java))
        }
        main_btn_setting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        main_btn_exit.setOnClickListener {
            val pref = getSharedPreferences("pref", MODE_PRIVATE)
            if(!pref.getBoolean("login",false)){
                Firebase.auth.signOut()
            }
            finish()
        }
    }
}