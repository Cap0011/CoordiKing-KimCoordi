package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.*
import com.team6.coordiking_kimcoordi.databinding.ActivityMyOutfitsBinding
import com.team6.coordiking_kimcoordi.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_my_outfits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class MyOutfitsActivity : AppCompatActivity(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    private var imageListBackUp = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    val database = Firebase.database.reference
    val storage = Firebase.storage
    lateinit var user: FirebaseUser
    var myOutfit: MutableList<Outfit> = arrayListOf()
    var myOutfitTagList: MutableList<OutfitTag> = arrayListOf()
    var myOutfitTagListBackUp: MutableList<OutfitTag> = arrayListOf()
    lateinit var binding: ActivityMyOutfitsBinding

    var colourArr = arrayOf(
            "red", "orange", "yellow", "green", "blue", "navy", "purple",
            "black", "white", "grey"
    )
    var styleArr = arrayOf("Vintage", "Tomboy", "Sporty", "Casual", "Chave", "Retro", "Grunge", "favourite");
    private val monthArr = hashMapOf(
            "Jan" to 1,
            "Feb" to 2,
            "Mar" to 3,
            "Apr" to 4,
            "May" to 5,
            "Jun" to 6,
            "Jul" to 7,
            "Aug" to 8,
            "Sep" to 9,
            "Oct" to 10,
            "Nov" to 11,
            "Dec" to 12
    )
    private val tagList = hashMapOf(
            "red" to 0,
            "orange" to 1,
            "yellow" to 2,
            "green" to 3,
            "blue" to 4,
            "navy" to 5,
            "purple" to 6,
            "black" to 7,
            "white" to 8,
            "grey" to 9,
            "Vintage" to 10,
            "Tomboy" to 11,
            "Sporty" to 12,
            "Casual" to 13,
            "Chave" to 14,
            "Retro" to 15,
            "Grunge" to 16,
            "favourite" to 17,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init view binding
        binding = ActivityMyOutfitsBinding.inflate(layoutInflater)
        user = Firebase.auth.currentUser!!

        setContentView(binding.root)
        setUpActionBar()
        // load Outfit from DB
        loadMyOutfit()
        // init adapter
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        // init recyclerview
        binding.recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        binding.recyclerView.adapter = galleryAdapter

        binding.addButton.setOnClickListener{
            // 플로팅 버튼
            // 갤러리에서 추가
            startActivityForResult(Intent(this,OutfitImageAddActivity::class.java),10)
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
        if(requestCode === 10 && resultCode === Activity.RESULT_OK){
            val currentTime = Calendar.getInstance().time
            val date = currentTime.toString()
            val dataName : String = data?.getStringExtra("dataName")!!
            val dataColor : Int = data?.getIntExtra("dataColor",0)!!
            val dataStyle : Int = data?.getIntExtra("dataStyle",0)!!
            val fav: Boolean = data?.getBooleanExtra("favourite", false)
            saveOutfit(user.uid, dataName, dataStyle, dataColor, fav)
            imageList.add(Image(dataName, dataColor, dataStyle, date))
            galleryAdapter.notifyDataSetChanged()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(tb_outfit)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_outfit.setNavigationOnClickListener{ onBackPressed()}
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu_outfit, menu)
        val searchView = menu.findItem(R.id.search_outfit).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchTag(query)
                return false
            }
        })
        return true
    }

    private fun searchTag(tag: String?) {
        if(tag==""){
            Toast.makeText(this, "Please enter the tag you want to look up", Toast.LENGTH_SHORT).show()
            return
        }
        val code = tagList[tag] ?: return
        if(imageListBackUp.size == 0){
            imageListBackUp.addAll(imageList)
            myOutfitTagListBackUp.addAll(myOutfitTagList)
        }
        else{
            imageList.clear()
            imageList.addAll(imageListBackUp)
            myOutfitTagList.clear()
            myOutfitTagList.addAll(myOutfitTagListBackUp)
        }
        var cnt = 0
        val total = myOutfitTagList.size
        for(n in 0 until total){
            if(!myOutfitTagList[n-cnt].tag[code!!]){
                imageList.removeAt(n - cnt)
                myOutfitTagList.removeAt(n-cnt)
                cnt++
            }
        }
        galleryAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sort_name_outfit -> {
                qsort(imageList, 0)
                galleryAdapter.notifyDataSetChanged()
            }
            R.id.sort_date_outfit -> {
                qsort(imageList, 1)
                galleryAdapter.notifyDataSetChanged()
            }
            R.id.sort_color_outfit -> {
                qsort(imageList, 2)
                galleryAdapter.notifyDataSetChanged()
            }
            R.id.sort_style_outfit -> {
                qsort(imageList, 3)
                galleryAdapter.notifyDataSetChanged()
            }
            R.id.sort_favourite -> {
                searchTag("favourite")
                galleryAdapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(adapterPosition: Int) {
        // 원본
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", adapterPosition)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "gallery")
    }

    private fun saveOutfit(uid: String, name:String, style: Int, colour: Int, fav: Boolean) {
        // 임시
        val currentTime = Calendar.getInstance().time
        val date = currentTime.toString()
        //
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("name").setValue(name)
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("style").setValue(style)
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("colour").setValue(colour)
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("fav").setValue(fav)
        database.child(uid).child("outfit").child(myOutfit.size.toString()).child("date").setValue(date)
        myOutfit.add(Outfit(name, style, colour, fav, date))
        database.child(uid).child("outfit").child("num").setValue(myOutfit.size.toString())
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
                        var name: String = ""
                        var style: Int = 0
                        var colour: Int = 0
                        var fav: Boolean = false
                        var date: String = ""
                        runBlocking {
                            database.child(uid).child("outfit").child(n.toString()).child("name").get().addOnSuccessListener {
                                name = it.value as String
                            }
                            database.child(uid).child("outfit").child(n.toString()).child("style").get().addOnSuccessListener {
                                style = (it.value as Long).toInt()
                            }
                            database.child(uid).child("outfit").child(n.toString()).child("colour").get().addOnSuccessListener{
                                colour = (it.value as Long).toInt()
                            }
                            database.child(uid).child("outfit").child(n.toString()).child("fav").get().addOnSuccessListener {
                                fav = it.value as Boolean
                            }
                            database.child(uid).child("outfit").child(n.toString()).child("date").get().addOnSuccessListener {
                                date = it.value as String
                            }
                        }.await()
                        myOutfit.add(Outfit(name, style, colour, fav, date))
                        imageList.add(Image(name, colour, style, date))
                        galleryAdapter.notifyDataSetChanged()
                        //set TagList
                        myOutfitTagList.add(n, OutfitTag())
                        myOutfitTagList[n].setIdxTrue(tagList[styleArr[style]]!!)
                        myOutfitTagList[n].setIdxTrue(tagList[colourArr[colour]]!!)
                        if(fav) myOutfitTagList[n].setIdxTrue(17)
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
                index = partitionbyName(array, left, right)
            1 ->
                index = partitionbyDate(array, left, right)
            2 ->
                index = partitionbyColour(array, left, right)
            3 ->
                index = partitionbyStyle(array, left, right)
        }
        if (left < index - 1) {
            qsort(array, sortingType,left, index - 1)
        }
        if (index < right) {
            qsort(array, sortingType,index, right)
        }
    }

    fun partitionbyName(array: ArrayList<Image>, start: Int, end: Int): Int {
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
                //swap taglist
                val t = myOutfitTagList[left]
                myOutfitTagList[left] = myOutfitTagList[right]
                myOutfitTagList[right] = t
                left++
                right--
            }
        }
        return left
    }

    fun partitionbyDate(array: ArrayList<Image>, start: Int, end: Int): Int {
        var left = start
        var right = end
        val pivot = array[(left + right) / 2]

        while (left <= right) {
            while (convertDate(array[left].date) < convertDate(pivot.date)) {
                left++
            }

            while (convertDate(array[right].date) > convertDate(pivot.date)) {
                right--
            }

            if (left <= right) {
                val temp = array[left]
                array[left] = array[right]
                array[right] = temp
                //swap taglist
                val t = myOutfitTagList[left]
                myOutfitTagList[left] = myOutfitTagList[right]
                myOutfitTagList[right] = t
                left++
                right--
            }
        }
        return left
    }

    fun convertDate(str: String): String{
        var token = str.split(' ')

        val year = token[5]
        val month = monthArr[token[1]].toString()
        val day = token[2]
        var time = token[3].split(':')
        var hour = time[0]
        var minute = time[1]
        var second = time[2]

        return year+month+day+hour+minute+second
    }

    fun partitionbyColour(array: ArrayList<Image>, start: Int, end: Int): Int {
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
                //swap taglist
                val t = myOutfitTagList[left]
                myOutfitTagList[left] = myOutfitTagList[right]
                myOutfitTagList[right] = t
                left++
                right--
            }
        }
        return left
    }

    fun partitionbyStyle(array: ArrayList<Image>, start: Int, end: Int): Int {
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
                //swap taglist
                val t = myOutfitTagList[left]
                myOutfitTagList[left] = myOutfitTagList[right]
                myOutfitTagList[right] = t
                left++
                right--
            }
        }
        return left
    }
}
