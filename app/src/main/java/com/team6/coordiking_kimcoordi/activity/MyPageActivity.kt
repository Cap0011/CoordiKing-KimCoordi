package com.team6.coordiking_kimcoordi.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_my_page.*

class MyPageActivity : AppCompatActivity() {
    private val curUser = Firebase.auth.currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        mypage_txt_name.setText(curUser.displayName)
        mypage_txt_email.setText("email: " + curUser.email)

        mypage_btn_logout.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(applicationContext, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        mypage_btn_changeInformation.setOnClickListener {
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
        }

        mypage_btn_deleteAccount.setOnClickListener {
            curUser.delete()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Your account has been successfully deleted.", Toast.LENGTH_LONG).show()
                    }
                }

            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
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
}