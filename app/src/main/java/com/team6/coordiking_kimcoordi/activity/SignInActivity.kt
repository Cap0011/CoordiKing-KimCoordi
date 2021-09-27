package com.team6.coordiking_kimcoordi.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        signin_btn_signup.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        signin_btn_signin.setOnClickListener {
            signIn()
        }
    }
    private fun signIn(){
        val email = signin_et_email.text.toString()
        val password = signin_et_password.text.toString()

        if(email==""||password==""){
            Toast.makeText(baseContext, "Please fill up all forms properly!", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Welcome!", Toast.LENGTH_SHORT).show()
                    saveLoginStatus()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        task.exception?.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }
    private fun saveLoginStatus(){
        val pref = getSharedPreferences("pref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        if(signin_chk.isChecked){
            editor.putBoolean("login", true)
        } else{
            editor.putBoolean("login", false)
        }
        editor.commit()
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}