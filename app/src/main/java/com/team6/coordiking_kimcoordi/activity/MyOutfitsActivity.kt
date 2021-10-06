package com.team6.coordiking_kimcoordi.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.*
import com.team6.coordiking_kimcoordi.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_my_outfits.*
import kotlinx.android.synthetic.main.activity_my_wardrobe.*
import java.util.*
import kotlin.collections.ArrayList

class MyOutfitsActivity : AppCompatActivity(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    val database = Firebase.database.reference
    lateinit var user: FirebaseUser
    var myOutfit: MutableList<Outfit> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_outfits)
        setUpActionBar()
    }
    private fun setUpActionBar(){
        setSupportActionBar(tb_outfit)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_outfit.setNavigationOnClickListener{ onBackPressed()}

        handleIntent(intent)

        // init adapter
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        // init recyclerview
        recyclerView1.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView1.adapter = galleryAdapter
        // load images
        loadImages()

        user = Firebase.auth.currentUser!!

        //test function(save)
        saveTest()

        loadMyOutfit()

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
    private fun saveOutfit(uid: String, url: String, style: Int, memo: String, date: String) {
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("url").setValue(url)
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("style").setValue(style)
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("memo").setValue(memo)
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("date").setValue(date)
        myOutfit.add(Outfit(url, style, memo, date))
        database.child(uid).child("outfit").child("num").setValue(myOutfit.size.toString())
    }

    private fun loadMyOutfit(){
        val uid = user.uid
        database.child(uid).child("outfit").child("num").get().addOnSuccessListener {
            var outfitNum = (it.value as String).toInt()
            for(n in 0 until outfitNum){
                var url: String = ""
                var style: Int = 0
                var memo: String = ""
                var date: String = ""
                database.child(uid).child("outfit").child(n.toString()).child("url").get().addOnSuccessListener {
                    url = it.value as String
                }
                database.child(uid).child("outfit").child(n.toString()).child("style").get().addOnSuccessListener {
                    style = (it.value as Long).toInt()
                }
                database.child(uid).child("outfit").child(n.toString()).child("memo").get().addOnSuccessListener {
                    memo = it.value as String
                }
                database.child(uid).child("outfit").child(n.toString()).child("date").get().addOnSuccessListener {
                    date = it.value as String
                }
                myOutfit.add(Outfit(url, style, memo, date))
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun saveTest(){
        saveOutfit(user.uid, "url0", 0, "memo", "date")
        saveOutfit(user.uid, "url1", 1, "memo", "date")
        saveOutfit(user.uid, "url2", 3, "memo", "date")
        saveOutfit(user.uid, "url3", 2, "memo", "date")
        saveOutfit(user.uid, "url4", 0, "memo", "date")
        saveOutfit(user.uid, "url5", 0, "memo", "date")
        saveOutfit(user.uid, "url6", 7, "memo", "date")
        saveOutfit(user.uid, "url7", 9, "memo", "date")
        saveOutfit(user.uid, "url8", 2, "memo", "date")
        saveOutfit(user.uid, "url9", 0, "memo", "date")
    }

    private fun loadTest(){
        for (outfit in myOutfit){
            Log.d("My Outfit", "${outfit.url}")
        }
    }
}