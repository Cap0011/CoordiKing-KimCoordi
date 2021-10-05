package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.Clothes
import com.team6.coordiking_kimcoordi.adapter.GalleryImageAdapter
import com.team6.coordiking_kimcoordi.adapter.GalleryImageClickListener
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.databinding.ActivityMyWardrobeBinding
import com.team6.coordiking_kimcoordi.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_my_wardrobe.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class MyWardrobeActivity : AppCompatActivity(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    val database = Firebase.database.reference
    val storage = Firebase.storage
    lateinit var user: FirebaseUser
    var myWardrobe: MutableList<Clothes> = arrayListOf()

    lateinit var binding: ActivityMyWardrobeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init view binding
        binding = ActivityMyWardrobeBinding.inflate(layoutInflater)
        user = Firebase.auth.currentUser!!

        setContentView(binding.root)
        setUpActionBar()
        // load Wardrobe from DB
        loadMyWardrobe()
        // init adapter
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        // init recyclerview
        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

        binding.addButton.setOnClickListener{
            // 플로팅 버튼
            // 갤러리에서 추가
            startActivityForResult(Intent(this,ImageAddActivity::class.java),10)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 10 && resultCode === Activity.RESULT_OK){
            val dataName : String = data?.getStringExtra("dataName")!!
            saveClothes(user.uid, "test", 0, 0, dataName)
            imageList.add(Image(dataName))
            galleryAdapter.notifyDataSetChanged()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(tb_wardrobe)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_wardrobe.setNavigationOnClickListener{ onBackPressed()}

        handleIntent(intent)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    override fun onClick(adapterPosition: Int) {
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", adapterPosition)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "gallery")
    }

    private fun saveClothes(uid: String, url: String, type: Int, colour: Int, name: String) {
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("url").setValue(url)
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("type").setValue(type)
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("colour").setValue(colour)
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("name").setValue(name)
        myWardrobe.add(Clothes(url, type, colour, name))
        database.child(uid).child("wardrobe").child("num").setValue(myWardrobe.size.toString())
    }

    private fun loadMyWardrobe(){
        imageList.clear()
        val uid = user.uid
        database.child(uid).child("wardrobe").child("num").get().addOnSuccessListener {
            it.value?.let {
                var clothesNum = (it as String).toInt()
                for(n in 0 until clothesNum){
                    CoroutineScope(Dispatchers.Main).async {
                        //데이터베이스 불러오기 동기처리
                        var url:String = ""
                        var type: Int = 0
                        var colour: Int = 0
                        var name: String = ""
                        database.child(uid).child("wardrobe").child(n.toString()).child("url").get().addOnSuccessListener{
                            url = it.value as String
                        }.await()
                        database.child(uid).child("wardrobe").child(n.toString()).child("type").get().addOnSuccessListener {
                            type = (it.value as Long).toInt()
                        }.await()
                        database.child(uid).child("wardrobe").child(n.toString()).child("colour").get().addOnSuccessListener {
                            colour = (it.value as Long).toInt()
                        }.await()
                        database.child(uid).child("wardrobe").child(n.toString()).child("name").get().addOnSuccessListener {
                            name = it.value as String
                        }.await()
                        myWardrobe.add(Clothes(url, type, colour, name))
                        imageList.add(Image(name))
                        galleryAdapter.notifyDataSetChanged()
                    }
                }
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }




    }
}