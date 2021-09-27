package com.team6.coordiking_kimcoordi.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.GalleryImageAdapter
import com.team6.coordiking_kimcoordi.adapter.GalleryImageClickListener
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_my_wardrobe.*

class MyWardrobeActivity : AppCompatActivity(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter

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
    }

    private fun loadImages() {
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841259-4d3737bd-a99f-41fb-907d-df28967a7a83.png", "sample0"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841263-cacd128e-aa15-4070-8329-5959892ca58c.png", "sample1"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841271-1679762c-061d-433d-a7b4-8ba690642a44.png", "sample2"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841273-6c34dca3-c86d-407a-b096-bdb743a3549a.png", "sample3"))
        imageList.add(Image("https://user-images.githubusercontent.com/59128435/134841275-921f4370-8cd2-4ee5-9d84-3b66a7a3b1a9.png", "sample4"))
        imageList.add(Image("https://i.ibb.co/L1m1NxP/girl.jpg", "Mountain Girl"))
        imageList.add(Image("https://i.ibb.co/wc9rSgw/desserts.jpg", "Desserts Table"))
        imageList.add(Image("https://i.ibb.co/wdrdpKC/kitten.jpg", "Kitten"))
        imageList.add(Image("https://i.ibb.co/dBCHzXQ/paris.jpg", "Paris Eiffel"))
        imageList.add(Image("https://i.ibb.co/JKB0KPk/pizza.jpg", "Pizza Time"))
        imageList.add(Image("https://i.ibb.co/VYYPZGk/salmon.jpg", "Salmon "))
        imageList.add(Image("https://i.ibb.co/JvWpzYC/sunset.jpg", "Sunset in Beach"))

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
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", adapterPosition)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "gallery")
    }
}