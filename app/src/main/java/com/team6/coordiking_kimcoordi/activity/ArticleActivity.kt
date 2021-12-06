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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.Clothes
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.adapter.WardrobeTag
import com.team6.coordiking_kimcoordi.databinding.ActivityArticleBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ArticleActivity : AppCompatActivity() {
    val database = Firebase.database.reference
    val storage = Firebase.storage
    lateinit var user: FirebaseUser

    lateinit var binding: ActivityArticleBinding

    var numRating:Int=0
    var avgRating:Float= 0F
    var userName: String = ""
    var postIdx : Int = -1
    var uid : String = ""
    var dataName : String = ""
    var title : String = ""
    var text : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = Firebase.auth.currentUser!!

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataName = intent.getStringExtra("dataName")!!
        title = intent.getStringExtra("title")!!
        text = intent.getStringExtra("text")!!
        userName = intent.getStringExtra("userName")!!
        uid = intent.getStringExtra("uid")!!
        postIdx = intent.getIntExtra("postIdx", 0)!!

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
        supportActionBar?.setTitle("Community")
        binding.postContents.setText(text)

        // 평균 별점
        database.child("ratings").child(postIdx.toString()).child("average").get().addOnSuccessListener {
            if(it.value!=null) binding.averageRating.setText(it.value.toString()+"점")
        }

//        database.child("ratings").child(postIdx.toString()).child(uid).child("rating").get().addOnSuccessListener {
//
//            it.value?.let{
//                binding.ratingBar.rating = (it as String).toFloat()
//            }
//
//        }

        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            saveRating(postIdx.toString(),rating, user.uid)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.article_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(user.displayName == userName ){
            when (item.itemId){

                R.id.edit -> {
                    val intent = Intent(this, PostEditActivity::class.java)
                    intent.putExtra("dataName",dataName)
                    intent.putExtra("title",title)
                    intent.putExtra("text",text)
                    intent.putExtra("uid",uid)
                    intent.putExtra("postIdx", postIdx)
                    startActivityForResult(intent, 10)
                }
                R.id.delete -> {
                    database.child("post").child(postIdx.toString()).child("status").setValue(false)
                    setResult( RESULT_CANCELED, intent)
                    finish()
                }
            }
        }
        else{
            Toast.makeText(this,"글쓴이가 아닙니다.",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 10 && resultCode === Activity.RESULT_OK){
            setResult( RESULT_CANCELED, intent)
            finish()
        }
    }

    private fun saveRating(postIdx :String, rating:Float, uid:String){
        database.child("ratings").child(postIdx).child("num").get().addOnSuccessListener {
            it.value?.let{
                var num: Int?
                if(it is Long) num = it.toInt()
                else num = (it as String).toInt()
                numRating = num
                database.child("ratings").child(postIdx).child(uid).child("rating").get().addOnSuccessListener {
                    it.value?.let{
                        //이미 평가 했을
                        Toast.makeText(this,"이미 평가하셨습니다.",Toast.LENGTH_SHORT).show()
                    }?: CoroutineScope(Dispatchers.Main).async {
                        //평가 안했을때
                        var avg: String = ""
                        runBlocking {
                            database.child("ratings").child(postIdx).child("average").get()
                                .addOnSuccessListener {
                                    if (it != null) avg = (it.value as String)
                                }
                        }.await<DataSnapshot?>()
                        avgRating = ((avg.toFloat() * numRating + rating) / (numRating + 1))
                        database.child("ratings").child(postIdx).child(uid).child("rating").setValue(String.format("%.1f", rating))
                        numRating++
                        database.child("ratings").child(postIdx).child("average").setValue(String.format("%.1f", avgRating))
                        database.child("ratings").child(postIdx).child("num").setValue(numRating)
                        binding.averageRating.setText(String.format("%.1f", avgRating)+"점")
                    }
                }
            }?: run {
                //최초 평가 일
                val Rating = String.format("%.1f", rating)
                database.child("ratings").child(postIdx).child(uid).child("rating").setValue(Rating)
                numRating++
                database.child("ratings").child(postIdx).child("average").setValue(Rating)
                database.child("ratings").child(postIdx).child("num").setValue(numRating)
                binding.averageRating.setText(Rating+"점")
            }

        }
    }
}