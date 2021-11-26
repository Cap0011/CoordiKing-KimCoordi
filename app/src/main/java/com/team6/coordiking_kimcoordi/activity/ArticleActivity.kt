package com.team6.coordiking_kimcoordi.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.team6.coordiking_kimcoordi.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {

    lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataName : String = intent.getStringExtra("dataName")!!
        val title : String = intent.getStringExtra("title")!!
        val text : String = intent.getStringExtra("text")!!
        val userName : String = intent.getStringExtra("userName")!!
        val uid : String = intent.getStringExtra("uid")!!

        //storage 이미지 다운로드
        val imgRef= MyApplication.storage
            .reference
            .child("${uid}/${dataName}.png").downloadUrl.addOnSuccessListener {  }.addOnSuccessListener {
                // load image
                Glide.with(applicationContext).load(it).into(binding.postImage)
            }.addOnCanceledListener {
                Log.d("kim","failed to download")
            }
        binding.author.setText(userName)
        binding.postTitle.setText(title)
        binding.postContents.setText(text)
    }
}