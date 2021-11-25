package com.team6.coordiking_kimcoordi.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team6.coordiking_kimcoordi.adapter.*
import com.team6.coordiking_kimcoordi.databinding.FragmentMyWardrobeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.RuntimeException
import kotlin.collections.ArrayList


class MyWardrobeFragment : Fragment(), GalleryImageClickListener {
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    val database = Firebase.database.reference
    val storage = Firebase.storage
    lateinit var user: FirebaseUser

    var myWardrobe: MutableList<Clothes> = arrayListOf()

    // Fragment에서 Activity로 데이터 전달 위함
    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(bundle: Bundle)
    }

    lateinit var fragmentListener: OnFragmentInteractionListener


    // User의 MyWardrobe에 저장된 사진들이 보임
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Log.d("MyWardrobeFragment", "onCreateView")
        super.onCreate(savedInstanceState)
        val binding = FragmentMyWardrobeBinding.inflate(inflater, container, false)
        user = Firebase.auth.currentUser!!

        // load Outfit from DB
        loadMyWardrobe()
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        binding.recyclerView.layoutManager = GridLayoutManager(getContext(), SPAN_COUNT)
        binding.recyclerView.adapter = galleryAdapter

        return binding.root
    }


    override fun onAttach(context: Context){
        // Log.d("MyWardrobeFragment", "onAttach")
        super.onAttach(context)

        if (context is OnFragmentInteractionListener){
            fragmentListener = context
        }else{
            throw RuntimeException(context.toString())
        }
    }

    // 사진을 선택하면 해당 정보를 Activity로 전달
    override fun onClick(adapterPosition: Int) {
        // Log.d("MyWardrobeFragment", "onClick")
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", adapterPosition)
        Toast.makeText(context, "사진을 선택했습니다.", Toast.LENGTH_SHORT).show()

        fragmentListener?.onFragmentInteraction(bundle)

        // flagment 종료
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.remove(this)
            ?.commit()
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
                        var url: String = ""
                        var type: Int = 0
                        var colour: Int = 0
                        var name: String = ""
                        var date: String = ""
                        runBlocking {
                            database.child(uid).child("wardrobe").child(n.toString()).child("url")
                                .get().addOnSuccessListener {
                                url = it.value as String
                            }
                            database.child(uid).child("wardrobe").child(n.toString()).child("type")
                                .get().addOnSuccessListener {
                                type = (it.value as Long).toInt()
                            }
                            database.child(uid).child("wardrobe").child(n.toString())
                                .child("colour").get().addOnSuccessListener {
                                colour = (it.value as Long).toInt()
                            }
                            database.child(uid).child("wardrobe").child(n.toString()).child("name")
                                .get().addOnSuccessListener {
                                name = it.value as String
                            }
                            database.child(uid).child("wardrobe").child(n.toString()).child("date")
                                .get().addOnSuccessListener {
                                date = it.value as String
                            }
                        }.await()
                        myWardrobe.add(Clothes(url, type, colour, name, date))
                        imageList.add(Image(name, colour, type, date))
                        Log.d("LoadMy", imageList.get(n).title)
                        galleryAdapter.notifyDataSetChanged()
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        Log.d("LoadMy", "종료")
    }
}