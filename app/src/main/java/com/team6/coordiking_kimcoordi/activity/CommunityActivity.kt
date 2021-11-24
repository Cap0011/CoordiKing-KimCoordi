package com.team6.coordiking_kimcoordi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.CommunityPostAdapter
import com.team6.coordiking_kimcoordi.adapter.GalleryImageAdapter
import com.team6.coordiking_kimcoordi.adapter.GalleryImageClickListener
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.databinding.ActivityCommunityBinding
import com.team6.coordiking_kimcoordi.databinding.ActivitySnapBinding
import com.team6.coordiking_kimcoordi.databinding.ItemCommunityBinding
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_snap.*

class CommunityActivity : AppCompatActivity(), GalleryImageClickListener {

    private val SPAN_COUNT = 3

    lateinit var CommunityAdapter: CommunityPostAdapter
    private var postList = ArrayList<Image>()

    lateinit var binding: ActivityCommunityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        setUpActionBar()

        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            startActivityForResult(Intent(this, PostAddActivity::class.java), 10)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        CommunityAdapter = CommunityPostAdapter(postList)
        CommunityAdapter.listener = this


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
        TODO("Not yet implemented")
    }
}