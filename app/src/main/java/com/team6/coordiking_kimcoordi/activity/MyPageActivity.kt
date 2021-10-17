package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
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
        mypage_txt_email.setText("email: " + curUser.email.toString())

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
            val newName = mypage_et_name.text.toString()
            val newPassword = mypage_et_newPassword.text.toString()
            val password = mypage_et_password.text.toString()

            if (password!="") {
                val credential = EmailAuthProvider
                    .getCredential(curUser.email.toString(), password)

                if (newName!="") {
                    curUser.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                changeName(newName)
                            } else {
                                Toast.makeText(baseContext, "Password is wrong.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                if (newPassword!="") {
                    curUser.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                changePassword(newPassword)
                            } else {
                                Toast.makeText(baseContext, "Password is wrong.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            else {
                Toast.makeText(baseContext, "Fill the password.", Toast.LENGTH_SHORT).show()
            }

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
        canChangeInformation = !canChangeInformation

        if (canChangeInformation) {
            mypage_et_name.setText("")
            mypage_et_password.setText("")
            mypage_et_name.setHint("Enter a new name.")
            mypage_et_password.setHint("Enter a current password.")
            mypage_et_newPassword.setHint("Enter a new password.")
            mypage_et_newPasswordAgain.setHint("Enter a new password again.")
            mypage_btn_changeInformation.setText("Cancel")
        }
        else {
            mypage_et_name.setText(curUser.displayName.toString())
            mypage_et_password.setText("password")
            mypage_et_name.setHint("")
            mypage_et_password.setHint("")
            mypage_et_newPassword.setHint("")
            mypage_et_newPasswordAgain.setHint("")
            mypage_btn_changeInformation.setText("Change Information")
        }

        mypage_et_name.isEnabled = canChangeInformation
        mypage_et_password.isEnabled = canChangeInformation
        mypage_et_newPassword.isEnabled = canChangeInformation
        mypage_et_newPassword.isVisible = canChangeInformation
        mypage_et_newPasswordAgain.isEnabled = canChangeInformation
        mypage_et_newPasswordAgain.isVisible = canChangeInformation
        mypage_btn_confirm.isEnabled = canChangeInformation
        mypage_btn_confirm.isVisible = canChangeInformation
    }

    private fun changeName(newName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
            photoUri = curUser.photoUrl
        }

        curUser.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Your name has been changed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun changePassword(newPassword: String) {
        val newPasswordAgain = mypage_et_newPasswordAgain.text.toString()

        if (newPassword == newPasswordAgain) {
            curUser.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Your password has been changed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            Toast.makeText(baseContext, "Passwords do not match.", Toast.LENGTH_SHORT).show()
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