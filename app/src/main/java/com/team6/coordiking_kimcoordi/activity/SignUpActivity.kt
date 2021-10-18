package com.team6.coordiking_kimcoordi.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database = Firebase.database.reference
    lateinit var curUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signup_btn_signup.setOnClickListener {
            signUp()
        }

        setUpActionBar()

        // Initialize Firebase Auth
        auth = Firebase.auth
    }
    private fun setUpActionBar(){
        setSupportActionBar(tb_signup)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_signup.setNavigationOnClickListener{ onBackPressed()}
    }
    private fun signUp(){
        val name = signup_et_name.text.toString()
        val email = signup_et_email.text.toString()
        val password = signup_et_password.text.toString()

        if(name==""||email==""||password==""){
            Toast.makeText(baseContext, "Please fill up all forms properly!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Success to sign up!", Toast.LENGTH_SHORT).show()
                    updateUI(user)
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                }
                            }

                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(baseContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            val intent = Intent(applicationContext, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            curUser = Firebase.auth.currentUser!!
            database.child(curUser.uid).child("wardrobe").child("num").setValue(0)
            database.child(curUser.uid).child("outfit").child("num").setValue(0)
        }
    }
}