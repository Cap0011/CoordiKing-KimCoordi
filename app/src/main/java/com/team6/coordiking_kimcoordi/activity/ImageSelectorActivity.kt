package com.team6.coordiking_kimcoordi.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.GalleryImageAdapter
import com.team6.coordiking_kimcoordi.adapter.GalleryImageClickListener
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.databinding.ActivityImageSelectorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ImageSelectorActivity : AppCompatActivity(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private var imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    lateinit var user: FirebaseUser
    val database = Firebase.database.reference
    val storage = Firebase.storage

    lateinit var binding: ActivityImageSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init view binding
        binding = ActivityImageSelectorBinding.inflate(layoutInflater)
        user = Firebase.auth.currentUser!!

        setContentView(binding.root)

        supportActionBar?.setTitle("Select a Photo")
        // load Outfit from DB
        loadMyOutfit()
        // init adapter
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        // init recyclerview
        binding.recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        binding.recyclerView.adapter = galleryAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.select_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onClick(adapterPosition: Int) {
        // Get selected image.
        val image = imageList.get(adapterPosition)
        // Test
        intent.putExtra("dataName",image.title)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun loadMyOutfit(){
        imageList.clear()
        val uid = user.uid
        database.child(uid).child("outfit").child("num").get().addOnSuccessListener {
            it.value?.let {
                var clothesNum: Int
                if(it is Long) clothesNum = it.toInt()
                else clothesNum = (it as String).toInt()
                for(n in 0 until clothesNum){
                    CoroutineScope(Dispatchers.Main).async {
                        //데이터베이스 불러오기 동기처리

                        var style: Int = 0
                        var name: String = ""
                        var date: String = ""
                        runBlocking {
                            database.child(uid).child("outfit").child(n.toString()).child("style").get().addOnSuccessListener {
                                style = (it.value as Long).toInt()
                            }
                            database.child(uid).child("outfit").child(n.toString()).child("name").get().addOnSuccessListener {
                                name = it.value as String
                            }
                            database.child(uid).child("outfit").child(n.toString()).child("date").get().addOnSuccessListener {
                                date = it.value as String
                            }
                        }.await()
                        imageList.add(Image(name,0, style,date))
                        galleryAdapter.notifyDataSetChanged()
                    }
                }
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
}