package com.team6.coordiking_kimcoordi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.activity.MyApplication
import com.team6.coordiking_kimcoordi.adapter.Image
import kotlinx.android.synthetic.main.fragment_random_offer.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class RandomOfferFragment : Fragment() {
    private var imageList = ArrayList<Image>()
    private var title = ArrayList<String>()
    private var clothestype = ArrayList<Int>()
    val database = Firebase.database.reference
    lateinit var user: FirebaseUser


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Log.d("RandomOfferFragment", "onCreateView")
        user = Firebase.auth.currentUser!!
        loadMyWardrobe()
        // Log.d("RandomOfferFragment", "종료")
        return inflater.inflate(R.layout.fragment_random_offer, container, false)
    }

    private fun randomTop(){
        // 랜덤으로 상의 선택
        val topList = ArrayList<String>()

        for (i in 0 until clothestype.size){
            if (clothestype.get(i) == 1) {
                topList.add(title.get(i))
            }
        }

        if(topList.size != 0){
            // 등록된 상의가 있는 경우
            val num = Random().nextInt(topList.size)
            val imgRef = MyApplication.storage
                .reference
                .child("${MyApplication.user!!.uid}/${topList.get(num)}.png").downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it).into(iv_random_top)
                }.addOnCanceledListener {
                    Log.d("RandomOfferFragment", "failed to download")
                }
        }
    }

    private fun randomBottom(){
        // 랜덤으로 하의 선택
        val bottomList = ArrayList<String>()

        for (i in 0 until clothestype.size){
            if (clothestype.get(i) == 2) {
                bottomList.add(title.get(i))
            }
        }

        if(bottomList.size != 0){
            // 등록된 하의가 있는 경우
            val num = Random().nextInt(bottomList.size)
            val imgRef = MyApplication.storage
                .reference
                .child("${MyApplication.user!!.uid}/${bottomList.get(num)}.png").downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it).into(iv_random_bottom)
                }.addOnCanceledListener {
                    Log.d("RandomOfferFragment", "failed to download")
                }
        }
    }

    private fun loadMyWardrobe() {
        // Log.d("MyWardrobeFragment", "loadMyWardrobe")
        imageList.clear()
        val uid = user.uid
        database.child(uid).child("wardrobe").child("num").get().addOnSuccessListener {
            it.value?.let {
                // 수정 -> 원본   var clothesNum: Int
                var clothesNum: Int?
                if (it is Long) clothesNum = it.toInt()
                else clothesNum = (it as String).toInt()
                for (n in 0 until clothesNum) {
                    CoroutineScope(Dispatchers.Main).async {
                        //데이터베이스 불러오기 동기처리
                        var type: Int = 0
                        var name: String = ""
                        runBlocking {
                            database.child(uid).child("wardrobe").child(n.toString()).child("type")
                                .get().addOnSuccessListener {
                                    type = (it.value as Long).toInt()
                                }
                            database.child(uid).child("wardrobe").child(n.toString()).child("name")
                                .get().addOnSuccessListener {
                                    name = it.value as String
                                }
                        }.await()
                        title.add(name)
                        clothestype.add(type)
                        if (n == clothesNum - 1) {
                            randomTop()
                            randomBottom()
                        }
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }
}