package com.team6.coordiking_kimcoordi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_snap.*

class SnapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        setUpActionBar()
    }
    private fun setUpActionBar(){
        setSupportActionBar(tb_snap)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_snap.setNavigationOnClickListener{ onBackPressed()}
    }
}