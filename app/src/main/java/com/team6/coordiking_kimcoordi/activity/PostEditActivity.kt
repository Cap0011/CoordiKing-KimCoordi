package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.databinding.ActivityPostAddBinding
import java.util.*


class PostEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostAddBinding
    val database = Firebase.database.reference
    lateinit var user: FirebaseUser

    var postIdx : Int = -1
    var uid : String = ""
    var dataName : String = ""
    var title : String = ""
    var text : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_add)

        user = Firebase.auth.currentUser!!

        binding = ActivityPostAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataName = intent.getStringExtra("dataName")!!
        title = intent.getStringExtra("title")!!
        text = intent.getStringExtra("text")!!
        uid = intent.getStringExtra("uid")!!
        postIdx = intent.getIntExtra("postIdx", 0)!!

        val imgRef= MyApplication.storage
            .reference
            .child("${uid}/${dataName}.png").downloadUrl.addOnSuccessListener {  }.addOnSuccessListener {
                // load image
                Glide.with(applicationContext).load(it).into(binding.addImageView)
            }.addOnCanceledListener {
                Log.d("kim","failed to download")
            }
        binding.postTitle.setText(title)
        binding.postText.setText(text)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId === R.id.menu_add_share){
            if(binding.addImageView.drawable !== null && binding.postTitle.text.isNotEmpty() && binding.postText.text.isNotEmpty()){
                // 계시물 저장
                savePost(user.uid, binding.postTitle.text.toString(), binding.postText.text.toString())
                Toast.makeText(this, "게시물이 수정되었습니다", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK, intent)
                finish()
            }else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun savePost(uid: String, title: String, text: String) {
        // 임시

        //
        database.child("post").child(postIdx.toString()).child("title").setValue(title)
        database.child("post").child(postIdx.toString()).child("text").setValue(text)


    }
}