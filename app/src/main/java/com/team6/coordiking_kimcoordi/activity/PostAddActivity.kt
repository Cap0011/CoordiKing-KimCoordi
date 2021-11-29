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

class PostAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostAddBinding
    val database = Firebase.database.reference
    lateinit var user: FirebaseUser
    var postSize = 0
    lateinit var dataName:String
    var style: Int = 0
    var colour: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_add)

        user = Firebase.auth.currentUser!!

        binding = ActivityPostAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database.child("post").child("num").get().addOnSuccessListener{
            it.value?.let {
                if(it is Long) postSize = it.toInt()
                else postSize = (it as String).toInt()
            }
        }

        startActivityForResult(Intent(this, ImageSelectorActivity::class.java), 10)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId === R.id.menu_add_share){
            if(binding.addImageView.drawable !== null && binding.postTitle.text.isNotEmpty() && binding.postText.text.isNotEmpty()){
                // 계시물 저장
                savePost(user.uid, binding.postTitle.text.toString(), binding.postText.text.toString(),dataName, style, colour)
                Toast.makeText(this, "게시물이 공유되었습니다", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK, intent)
                finish()
            }else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode===10 && resultCode=== Activity.RESULT_OK) {
            dataName = data?.getStringExtra("dataName")!!
            colour = data?.getIntExtra("dataColour", 0)
            style = data?.getIntExtra("dataStyle", 0)
            //storage 이미지 다운로드
            val imgRef= MyApplication.storage
                .reference
                .child("${MyApplication.user!!.uid}/${dataName}.png").downloadUrl.addOnSuccessListener {  }.addOnSuccessListener {
                    // load image
                    Glide.with(applicationContext).load(it).into(binding.addImageView)
                }.addOnCanceledListener {
                    Log.d("kim","failed to download")
                }
            }
    }

    private fun savePost(uid: String, title: String, text: String, dataName: String, style: Int, colour: Int) {
        // 임시
        val currentTime = Calendar.getInstance().time
        val date = currentTime.toString()
        val userName = user.displayName
        val num = postSize.toString()
        //
        database.child("post").child(num).child("uid").setValue(uid)
        database.child("post").child(num).child("title").setValue(title)
        database.child("post").child(num).child("text").setValue(text)
        database.child("post").child(num).child("data-name").setValue(dataName)
        database.child("post").child(num).child("user-name").setValue(userName)
        database.child("post").child(num).child("date").setValue(date)
        database.child("post").child(num).child("style").setValue(style)
        database.child("post").child(num).child("colour").setValue(colour)
        postSize++
        database.child("post").child("num").setValue(postSize.toString())
    }

}