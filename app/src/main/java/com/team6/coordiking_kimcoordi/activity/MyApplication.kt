package com.team6.coordiking_kimcoordi.activity

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyApplication : MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var db: FirebaseDatabase
        lateinit var storage: FirebaseStorage
        lateinit var user: FirebaseUser
        var is_guideline_disable: Boolean = false // add by 박재한 (가이드라인 토글 boolean)
    }

    override fun onCreate() {
        super.onCreate()
        auth =  Firebase.auth

        db= FirebaseDatabase.getInstance()
        storage= Firebase.storage
        user = Firebase.auth.currentUser!!
    }
}