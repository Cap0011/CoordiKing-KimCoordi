package com.team6.coordiking_kimcoordi.activity

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_my_page.*

class MyPageActivity : AppCompatActivity() {
    var isNameChecked = false
    var isPasswordChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)
        updateUI()

        mypage_btn_logout.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(applicationContext, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        mypage_btn_change_name.setOnClickListener {
            if(!isNameChecked) makeNameVisible()
            else makeNameGone()
        }
        mypage_btn_confirm_name.setOnClickListener {
            val str = mypage_et_name.text.toString()
            if(str == ""){
                Toast.makeText(this, "Please enter your new name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = Firebase.auth.currentUser
            val profileUpdates = userProfileChangeRequest {
                displayName = str
            }

            user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            updateUI()
                            Toast.makeText(this, "Your name has been changed to ${user.displayName}", Toast.LENGTH_SHORT).show()
                        }
                    }
            makeNameGone()
        }

        mypage_btn_change_pwd.setOnClickListener {
            makeNewPasswordGone()
            if(!isPasswordChecked) makeCurrentPasswordVisible()
            else makeCurrentPasswordGone()
        }
        mypage_btn_confirm_currentPassword.setOnClickListener {
            val pwd = mypage_et_currentPassword.text.toString()
            if(pwd!="") {
                val user = Firebase.auth.currentUser!!
                val email = user.email.toString()
                val credential = EmailAuthProvider.getCredential(email, pwd)

                user.reauthenticate(credential)
                        .addOnSuccessListener {
                            makeCurrentPasswordGone()
                            makeNewPasswordVisible()
                        }
                        .addOnFailureListener {
                            mypage_et_currentPassword.setText("")
                            Toast.makeText(this, "You entered wrong password!", Toast.LENGTH_SHORT).show()
                        }
            } else{
                Toast.makeText(this, "Please enter your current password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
        mypage_btn_confirm_newPassword.setOnClickListener {
            val pwd0 = mypage_et_newPassword0.text.toString()
            val pwd1 = mypage_et_newPassword1.text.toString()

            if(pwd0 != pwd1){
                Toast.makeText(this, "Please enter same password", Toast.LENGTH_SHORT).show()
            } else if(pwd0 == ""){
                Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show()
            } else{
                val user = Firebase.auth.currentUser

                user!!.updatePassword(pwd1)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Your password has been updated successfully", Toast.LENGTH_SHORT).show()
                                isPasswordChecked = false
                                makeNewPasswordGone()
                            } else{
                                Toast.makeText(this, "Your password should be at least 6 characters!", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
        setUpActionBar()
    }
    private fun updateUI(){
        mypage_tv_displayName.setText("USER NAME: "+Firebase.auth.currentUser!!.displayName)
        mypage_tv_displayEmail.setText("EMAIL ADDRESS:\n"+Firebase.auth.currentUser!!.email)
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
    private fun makeNameVisible(){
        mypage_tv_name.visibility = View.VISIBLE
        mypage_et_name.visibility = View.VISIBLE
        mypage_btn_confirm_name.visibility = View.VISIBLE
        isNameChecked = !isNameChecked
    }
    private fun makeNameGone(){
        mypage_tv_name.visibility = View.GONE
        mypage_et_name.visibility = View.GONE
        mypage_btn_confirm_name.visibility = View.GONE
        isNameChecked = !isNameChecked
        mypage_et_name.setText("")
    }
    private fun makeCurrentPasswordVisible(){
        mypage_tv_currentPassword.visibility = View.VISIBLE
        mypage_et_currentPassword.visibility = View.VISIBLE
        mypage_btn_confirm_currentPassword.visibility = View.VISIBLE
        isPasswordChecked = !isPasswordChecked
    }
    private fun makeCurrentPasswordGone(){
        mypage_tv_currentPassword.visibility = View.GONE
        mypage_et_currentPassword.visibility = View.GONE
        mypage_btn_confirm_currentPassword.visibility = View.GONE
        isPasswordChecked = !isPasswordChecked
        mypage_et_currentPassword.setText("")
    }
    private fun makeNewPasswordVisible(){
        mypage_tv_newPassword0.visibility = View.VISIBLE
        mypage_et_newPassword0.visibility = View.VISIBLE
        mypage_tv_newPassword1.visibility = View.VISIBLE
        mypage_et_newPassword1.visibility = View.VISIBLE
        mypage_btn_confirm_newPassword.visibility = View.VISIBLE
    }
    private fun makeNewPasswordGone(){
        mypage_tv_newPassword0.visibility = View.GONE
        mypage_et_newPassword0.visibility = View.GONE
        mypage_tv_newPassword1.visibility = View.GONE
        mypage_et_newPassword1.visibility = View.GONE
        mypage_btn_confirm_newPassword.visibility = View.GONE
        mypage_et_newPassword0.setText("")
        mypage_et_newPassword1.setText("")
    }
}