package com.team6.coordiking_kimcoordi.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.*
import com.team6.coordiking_kimcoordi.databinding.ActivityMyWardrobeBinding
import com.team6.coordiking_kimcoordi.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_my_wardrobe.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class MyWardrobeActivity : AppCompatActivity(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    val database = Firebase.database.reference
    val storage = Firebase.storage
    lateinit var user: FirebaseUser
    var myWardrobe: MutableList<Clothes> = arrayListOf()
    var myWardrobeTagList: MutableList<WardrobeTag> = arrayListOf()

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
        binding.recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        binding.recyclerView.adapter = galleryAdapter

        binding.addButton.setOnClickListener{
            // 플로팅 버튼
            // 갤러리에서 추가
            startActivityForResult(Intent(this,ImageAddActivity::class.java),10)
        }

        //added by 박재한
        var intent = intent
        var anotherdata: String? = intent.getStringExtra("snap")

        if (anotherdata !== null) {
            binding.addButton.callOnClick()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 10 && resultCode === RESULT_OK){
            val currentTime = Calendar.getInstance().time
            val date = currentTime.toString()
            val dataName : String = data?.getStringExtra("dataName")!!
            val dataColor : Int = data?.getIntExtra("dataColor",0)!!
            val dataType : Int = data?.getIntExtra("dataType",0)!!
            saveClothes(user.uid, "test", dataType, dataColor, dataName)
            imageList.add(Image(dataName,dataColor,dataType,date))
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
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sort_name ->
                qsort(imageList,0)
            R.id.sort_date ->
                qsort(imageList,1)
            R.id.sort_color ->
                qsort(imageList,2)
            R.id.sort_type ->
                qsort(imageList,3)
        }
        galleryAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
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
        // 임시
        val currentTime = Calendar.getInstance().time
        val date = currentTime.toString()
        //
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("url").setValue(url)
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("type").setValue(type)
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("colour").setValue(colour)
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("name").setValue(name)
        database.child(uid).child("wardrobe").child(myWardrobe.size.toString()).child("date").setValue(date)
        myWardrobe.add(Clothes(url, type, colour, name, date))
        database.child(uid).child("wardrobe").child("num").setValue(myWardrobe.size.toString())
    }

    private fun loadMyWardrobe(){
        imageList.clear()
        val uid = user.uid
        database.child(uid).child("wardrobe").child("num").get().addOnSuccessListener {
            it.value?.let {
                // 수정 -> 원본   var clothesNum: Int
                var clothesNum: Int?
                if(it is Long) clothesNum = it.toInt()
                else clothesNum = (it as String).toInt()
                for(n in 0 until clothesNum){
                    CoroutineScope(Dispatchers.Main).async {
                        //데이터베이스 불러오기 비동기처리(병렬)
                       var url:String = ""
                        var type: Int = 0
                        var colour: Int = 0
                        var name: String = ""
                        var date: String = ""
                        runBlocking {
                            database.child(uid).child("wardrobe").child(n.toString()).child("url").get().addOnSuccessListener{
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
                            database.child(uid).child("wardrobe").child(n.toString()).child("date").get().addOnSuccessListener {
                                date = it.value as String
                            }
                        }.await()
                        myWardrobe.add(Clothes(url, type, colour, name,date))
                        imageList.add(Image(name,colour,type,date))
                        galleryAdapter.notifyDataSetChanged()
                    }
                }
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun qsort(array: ArrayList<Image>, sortingType: Int,left: Int = 0, right: Int = array.size - 1) {
        var index = 0
        when (sortingType){
            0 ->
                index = partition0(array, left, right)
            1 ->
                index = partition1(array, left, right)
            2 ->
                index = partition2(array, left, right)
            3 ->
                index = partition3(array, left, right)
        }
        if (left < index - 1) {
            qsort(array, sortingType,left, index - 1)
        }
        if (index < right) {
            qsort(array, sortingType,index, right)
        }
    }

    fun partition0(array: ArrayList<Image>, start: Int, end: Int): Int {
        var left = start
        var right = end
        val pivot = array[(left + right) / 2]

        while (left <= right) {
            while (array[left].title < pivot.title) {
                left++
            }

            while (array[right].title > pivot.title) {
                right--
            }

            if (left <= right) {
                val temp = array[left]
                array[left] = array[right]
                array[right] = temp
                left++
                right--
            }
        }
        return left
    }

    fun partition1(array: ArrayList<Image>, start: Int, end: Int): Int {
        var left = start
        var right = end
        val pivot = array[(left + right) / 2]

        while (left <= right) {
            while (array[left].date < pivot.date) {
                left++
            }

            while (array[right].date > pivot.date) {
                right--
            }

            if (left <= right) {
                val temp = array[left]
                array[left] = array[right]
                array[right] = temp
                left++
                right--
            }
        }
        return left
    }

    fun partition2(array: ArrayList<Image>, start: Int, end: Int): Int {
        var left = start
        var right = end
        val pivot = array[(left + right) / 2]

        while (left <= right) {
            while (array[left].color < pivot.color) {
                left++
            }

            while (array[right].color > pivot.color) {
                right--
            }

            if (left <= right) {
                val temp = array[left]
                array[left] = array[right]
                array[right] = temp
                left++
                right--
            }
        }
        return left
    }

    fun partition3(array: ArrayList<Image>, start: Int, end: Int): Int {
        var left = start
        var right = end
        val pivot = array[(left + right) / 2]

        while (left <= right) {
            while (array[left].type < pivot.type) {
                left++
            }

            while (array[right].type > pivot.type) {
                right--
            }

            if (left <= right) {
                val temp = array[left]
                array[left] = array[right]
                array[right] = temp
                left++
                right--
            }
        }
        return left
    }
}