package com.team6.coordiking_kimcoordi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    private val curUser = Firebase.auth.currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setting_btn_changeEmail.setOnClickListener {
            val newEmail = setting_et_email.text.toString()

            if(newEmail==""){
                Toast.makeText(baseContext, "Please enter a new email.", Toast.LENGTH_SHORT).show()
            }
            else {
                curUser.updateEmail(newEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "Your email has been changed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        setting_btn_changePassword.setOnClickListener {
            val newPassword = setting_et_password.text.toString()

            if (newPassword=="") {
                Toast.makeText(baseContext, "Please enter a new password.", Toast.LENGTH_SHORT).show()
            }
            else {
                curUser.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "Your password has been changed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        setUpActionBar()
    }
    private fun setUpActionBar(){
        setSupportActionBar(tb_setting)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_setting.setNavigationOnClickListener{ onBackPressed()}
    }
}