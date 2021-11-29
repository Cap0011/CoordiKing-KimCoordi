package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
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
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.CommunityPostAdapter
import com.team6.coordiking_kimcoordi.adapter.GalleryImageClickListener
import com.team6.coordiking_kimcoordi.adapter.Post
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

    lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        setUpActionBar()

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

        startActivity(intent)
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
                        }.await()
                        postList.add(Post(dataName,title,text, userName, uid, date))
                        CommunityAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("da","text change")
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("da","text submit")
                searchArticle(query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sort_name -> {
            }
            R.id.sort_date -> {
            }
            R.id.sort_color -> {
            }
            R.id.sort_type -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun searchArticle(article: String?) {

    }
}