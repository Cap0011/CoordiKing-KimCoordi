package com.team6.coordiking_kimcoordi.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.*
import com.team6.coordiking_kimcoordi.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_my_wardrobe.*
import kotlinx.android.synthetic.main.activity_simulator.*

class MyWardrobeActivity : AppCompatActivity(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    val database = Firebase.database.reference
    lateinit var user: FirebaseUser
    var myWardrobe: MutableList<Clothes> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_wardrobe)
        setUpActionBar()

        // init adapter
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        // init recyclerview
        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter
        // load images
        loadImages()

        user = Firebase.auth.currentUser!!

        //test function(save)
        saveTest()

        //loadMyWardrobe()

        //test function(load)
        loadTest()
    }

    private fun loadImages() {
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841259-4d3737bd-a99f-41fb-907d-df28967a7a83.png", "sample0"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841263-cacd128e-aa15-4070-8329-5959892ca58c.png", "sample1"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841271-1679762c-061d-433d-a7b4-8ba690642a44.png", "sample2"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841273-6c34dca3-c86d-407a-b096-bdb743a3549a.png", "sample3"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841275-921f4370-8cd2-4ee5-9d84-3b66a7a3b1a9.png", "sample4"))

        galleryAdapter.notifyDataSetChanged()
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
        // 수정
        if (intent.hasExtra("SimulatorActivity")) {
            val intent = Intent(this, SimulatorActivity::class.java)
            intent.putExtra("good", "good")
            Toast.makeText(this, "okay", Toast.LENGTH_SHORT).show()
            finish()

        }else {
            val bundle = Bundle()
            bundle.putSerializable("images", imageList)
            bundle.putInt("position", adapterPosition)
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val galleryFragment = GalleryFullscreenFragment()
            galleryFragment.arguments = bundle
            galleryFragment.show(fragmentTransaction, "gallery")
        }
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
        val uid = user.uid
        database.child(uid).child("wardrobe").child("num").get().addOnSuccessListener {
            var clothesNum = (it.value as String).toInt()
            for(n in 0 until clothesNum){
                var url:String = ""
                var type: Int = 0
                var colour: Int = 0
                var name: String = ""
                database.child(uid).child("wardrobe").child(n.toString()).child("url").get().addOnSuccessListener {
                    url = it.value as String
                }
                database.child(uid).child("wardrobe").child(n.toString()).child("type").get().addOnSuccessListener {
                    type = (it.value as Long).toInt()
                }
                database.child(uid).child("wardrobe").child(n.toString()).child("colour").get().addOnSuccessListener {
                    colour = (it.value as Long).toInt()
                }
                database.child(uid).child("wardrobe").child(n.toString()).child("name").get().addOnSuccessListener {
                    name = it.value as String
                }
                myWardrobe.add(Clothes(url, type, colour, name))
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun saveTest(){
        saveClothes(user.uid, "url0", 0, 1, "name0")
        saveClothes(user.uid, "url1", 1, 1, "name1")
        saveClothes(user.uid, "url2", 3, 0, "name2")
        saveClothes(user.uid, "url3", 2, 1, "name3")
        saveClothes(user.uid, "url4", 0, 1, "name4")
        saveClothes(user.uid, "url5", 0, 5, "name5")
        saveClothes(user.uid, "url6", 7, 1, "name6")
        saveClothes(user.uid, "url7", 9, 1, "name7")
        saveClothes(user.uid, "url8", 2, 9, "name8")
        saveClothes(user.uid, "url9", 0, 9, "name9")
    }

    private fun loadTest(){
        for (clothes in myWardrobe){
            Log.d("My Wardrobe", "${clothes.url}")
        }
    }
}