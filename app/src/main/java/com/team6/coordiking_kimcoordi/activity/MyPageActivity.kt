package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_setting.*
import java.lang.Exception

class MyPageActivity : AppCompatActivity() {
    private val curUser = Firebase.auth.currentUser!!
    private var canChangeInformation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        mypage_et_name.setText(curUser.displayName.toString())
        mypage_et_email.setText("email: " + curUser.email.toString())

        mypage_btn_logout.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(applicationContext, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        mypage_btn_changeInformation.setOnClickListener {
            setChangeInformation()
        }

        mypage_btn_confirm.setOnClickListener {
            changeInformation()
            setChangeInformation()
        }

        mypage_btn_deleteAccount.setOnClickListener {
            deleteAccount()
        }

        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(tb_mypage)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_mypage.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun setChangeInformation() {
        if (canChangeInformation) {
            mypage_et_name.setText(curUser.displayName.toString())
            mypage_et_email.setText("email: " + curUser.email.toString())
            mypage_et_password.setText("password")
        }
        else {
            mypage_et_name.setText("")
            mypage_et_email.setText("")
            mypage_et_password.setText("")
        }
        canChangeInformation = !canChangeInformation

        mypage_et_name.isEnabled = canChangeInformation
        mypage_et_email.isEnabled = canChangeInformation
        mypage_et_password.isEnabled = canChangeInformation
        mypage_btn_confirm.isEnabled = canChangeInformation
        mypage_btn_confirm.isVisible = canChangeInformation
    }

    private fun changeInformation() {
        val newEmail = mypage_et_email.text.toString()
        val newPassword = mypage_et_password.text.toString()

        if (newEmail!="") {
            curUser.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Your email has been changed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        if (newPassword!="") {
            curUser.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Your password has been changed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun deleteAccount() {
        curUser.delete()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Your account has been successfully deleted.", Toast.LENGTH_LONG).show()
                }
            }

        val intent = Intent(applicationContext, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}