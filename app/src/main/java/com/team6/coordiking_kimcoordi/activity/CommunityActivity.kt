package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.common.api.Status.RESULT_CANCELED
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.*
import com.team6.coordiking_kimcoordi.databinding.ActivityCommunityBinding
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class CommunityActivity : AppCompatActivity(), GalleryImageClickListener {

    val database = Firebase.database.reference
    lateinit var user: FirebaseUser
    var postSize = 0

    private val SPAN_COUNT = 3

    lateinit var CommunityAdapter: CommunityPostAdapter
    private var postList = ArrayList<Post>()
    private var postListBackUp = ArrayList<Post>()
    lateinit var binding: ActivityCommunityBinding

    var myCommunityTagList: MutableList<OutfitTag> = arrayListOf()
    var myCommunityTagListBackUp: MutableList<OutfitTag> = arrayListOf()

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
        user = Firebase.auth.currentUser!!
        binding = ActivityCommunityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setUpActionBar()

        loadMyPost()

        CommunityAdapter = CommunityPostAdapter(postList)
        CommunityAdapter.listener = this

        binding.recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        binding.recyclerView.adapter = CommunityAdapter

        binding.addButton.setOnClickListener{
            startActivityForResult(Intent(this, PostAddActivity::class.java), 10)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 10 && resultCode === Activity.RESULT_OK){
            loadMyPost()
        }
        if(requestCode === 20 && resultCode === Activity.RESULT_CANCELED){
            loadMyPost()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu_community, menu)
        val searchView = menu.findItem(R.id.search_community).actionView as SearchView
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
        if(postListBackUp.size == 0){
            postListBackUp.addAll(postList)
            myCommunityTagListBackUp.addAll(myCommunityTagList)
        }
        else{
            postList.clear()
            postList.addAll(postListBackUp)
            myCommunityTagList.clear()
            myCommunityTagList.addAll(myCommunityTagListBackUp)
        }
        var cnt = 0
        val total = myCommunityTagList.size
        for(n in 0 until total){
            if(!myCommunityTagList[n-cnt].tag[code!!]){
                postList.removeAt(n - cnt)
                myCommunityTagList.removeAt(n-cnt)
                cnt++
            }
        }
        CommunityAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sort_name_comm -> {
                qsort(postList, 0)
                CommunityAdapter.notifyDataSetChanged()
            }
            R.id.sort_date_comm -> {
                qsort(postList, 1)
                CommunityAdapter.notifyDataSetChanged()
            }
            R.id.sort_color_comm -> {
                qsort(postList, 2)
                CommunityAdapter.notifyDataSetChanged()
            }
            R.id.sort_style_comm -> {
                qsort(postList, 3)
                CommunityAdapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar(){
        setSupportActionBar(tb_community)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        tb_community.setNavigationOnClickListener{ onBackPressed() }
    }



    override fun onClick(adapterPosition: Int) {
        // Get selected image.
        val image = postList.get(adapterPosition)
        // Test
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("dataName",image.dataName)
        intent.putExtra("title",image.title)
        intent.putExtra("text",image.text)
        intent.putExtra("userName",image.userName)
        intent.putExtra("uid",image.uid)
        intent.putExtra("dataStyle", image.style)
        intent.putExtra("dataColour", image.colour)
        intent.putExtra("postIdx", image.postIdx)
        startActivityForResult(intent, 20)
    }

    private fun loadMyPost(){
        postList.clear()
        database.child("post").child("num").get().addOnSuccessListener{
            it.value?.let {
                if(it is Long) postSize = it.toInt()
                else postSize = (it as String).toInt()
                for(n in 0 until postSize){
                    CoroutineScope(Dispatchers.Main).async {
                        //데이터베이스 불러오기 비동기처리(병렬)
                        var dataName: String = ""
                        var title: String = ""
                        var text: String = ""
                        var date: String = ""
                        var userName: String = ""
                        var uid: String = ""
                        var style: Int = 0
                        var colour: Int = 0
                        var postIdx: Int = n
                        var status: Boolean = false
                        runBlocking {
                            database.child("post").child(n.toString()).child("data-name").get().addOnSuccessListener{
                                if(it!=null) dataName = it.value as String
                            }
                            database.child("post").child(n.toString()).child("title").get().addOnSuccessListener{
                                if(it!=null) title = it.value as String
                            }
                            database.child("post").child(n.toString()).child("text").get().addOnSuccessListener{
                                if(it!=null) text = it.value as String
                            }
                            database.child("post").child(n.toString()).child("date").get().addOnSuccessListener{
                                if(it!=null) date = it.value as String
                            }
                            database.child("post").child(n.toString()).child("user-name").get().addOnSuccessListener{
                                if(it!=null) userName = it.value as String
                            }
                            database.child("post").child(n.toString()).child("uid").get().addOnSuccessListener{
                                if(it!=null) uid = it.value as String
                            }
                            database.child("post").child(n.toString()).child("style").get().addOnSuccessListener{
                                if(it!=null) style = (it.value as Long).toInt()
                            }
                            database.child("post").child(n.toString()).child("colour").get().addOnSuccessListener{
                                if(it!=null) colour = (it.value as Long).toInt()
                            }
                            database.child("post").child(n.toString()).child("status").get().addOnSuccessListener{
                                if(it!=null) status = it.value as Boolean
                            }
                        }.await()
                        if(status){
                            postList.add(Post(postIdx,dataName,title,text, userName, uid, date, style, colour))
                            CommunityAdapter.notifyDataSetChanged()
                            //Tag
                            myCommunityTagList.add(n, OutfitTag())
                            myCommunityTagList[n].setIdxTrue(tagList[styleArr[style]]!!)
                            myCommunityTagList[n].setIdxTrue(tagList[colourArr[colour]]!!)
                        }
                    }
                }
            }
        }
    }
    fun qsort(array: ArrayList<Post>, sortingType: Int, left: Int = 0, right: Int = array.size - 1) {
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

    fun partitionbyName(array: ArrayList<Post>, start: Int, end: Int): Int {
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
                val t = myCommunityTagList[left]
                myCommunityTagList[left] = myCommunityTagList[right]
                myCommunityTagList[right] = t
                left++
                right--
            }
        }
        return left
    }

    fun partitionbyDate(array: ArrayList<Post>, start: Int, end: Int): Int {
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
                val t = myCommunityTagList[left]
                myCommunityTagList[left] = myCommunityTagList[right]
                myCommunityTagList[right] = t
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

    fun partitionbyColour(array: ArrayList<Post>, start: Int, end: Int): Int {
        var left = start
        var right = end
        val pivot = array[(left + right) / 2]

        while (left <= right) {
            while (array[left].colour < pivot.colour) {
                left++
            }

            while (array[right].colour > pivot.colour) {
                right--
            }

            if (left <= right) {
                val temp = array[left]
                array[left] = array[right]
                array[right] = temp
                //swap taglist
                val t = myCommunityTagList[left]
                myCommunityTagList[left] = myCommunityTagList[right]
                myCommunityTagList[right] = t
                left++
                right--
            }
        }
        return left
    }

    fun partitionbyStyle(array: ArrayList<Post>, start: Int, end: Int): Int {
        var left = start
        var right = end
        val pivot = array[(left + right) / 2]

        while (left <= right) {
            while (array[left].style < pivot.style) {
                left++
            }

            while (array[right].style > pivot.style) {
                right--
            }

            if (left <= right) {
                val temp = array[left]
                array[left] = array[right]
                array[right] = temp
                //swap taglist
                val t = myCommunityTagList[left]
                myCommunityTagList[left] = myCommunityTagList[right]
                myCommunityTagList[right] = t
                left++
                right--
            }
        }
        return left
    }
}