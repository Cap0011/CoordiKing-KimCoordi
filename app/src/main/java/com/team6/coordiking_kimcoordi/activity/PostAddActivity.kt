package com.team6.coordiking_kimcoordi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.databinding.ActivityCommunityBinding
import com.team6.coordiking_kimcoordi.databinding.ActivityPostAddBinding
import com.team6.coordiking_kimcoordi.databinding.ItemCommunityBinding

class PostAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_add)

        binding = ActivityPostAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}