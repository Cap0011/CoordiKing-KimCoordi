package com.team6.coordiking_kimcoordi.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.StorageReference
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.databinding.ActivityImageAddBinding
import java.io.ByteArrayOutputStream

class ImageAddActivity : AppCompatActivity() {
//    var myWardrobe: MutableList<Clothes> = arrayListOf()
    lateinit var binding: ActivityImageAddBinding
    lateinit var bitmap: Bitmap
    lateinit var colorSpinner: Spinner
    lateinit var typeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //바인딩 초기화
        binding= ActivityImageAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // colorSpinner Init
        colorSpinner = binding.colorSpinner
        colorSpinner.adapter = ArrayAdapter.createFromResource(this,R.array.color_picker_array, android.R.layout.simple_spinner_item)
        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    //Black
                    0->{

                    }
                    //White
                    1->{

                    }
                    else -> {

                    }
                }
            }
        }
        //typeSpinner innit
        typeSpinner = binding.typeSpinner
        typeSpinner.adapter = ArrayAdapter.createFromResource(this,R.array.type_picker_array, android.R.layout.simple_spinner_item)
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    //type1
                    0->{

                    }
                    //type2
                    1->{

                    }
                    else -> {

                    }
                }
            }
        }
        // 갤러리 열기
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent,10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode===10 && resultCode=== Activity.RESULT_OK) {
            Glide
                .with(getApplicationContext())
                .load(data?.data)
                .apply(RequestOptions().override(250, 200))
                .centerCrop()
                .into(binding.addImageView)

            try {
                //비율 계산, 지정
                val option = BitmapFactory.Options()
                option.inSampleSize = 4
                //이미지 불러오기
                var inputStream = contentResolver.openInputStream(data!!.data!!)
                bitmap = BitmapFactory.decodeStream(inputStream, null, option)!!
                inputStream!!.close()
                inputStream = null

            }catch (e: Exception) {
                    e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId === R.id.menu_add_save){
            if(binding.addImageView.drawable !== null && binding.addEditView.text.isNotEmpty()){
                // 이미지 storage 업로드
                uploadImage(MyApplication.user!!.uid, binding.addEditView.text.toString())
                Toast.makeText(this, "데이터가 저장되었습니다", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadImage(uid:String, name: String){
        //add............................
        val storage = MyApplication.storage
        //storage 를 참조하는 StorageReference 를 만든다.
        val storageRef: StorageReference = storage.reference
        //실제 업로드하는 파일을 참조하는 StorageReference 를 만든다.
        val imgRef: StorageReference = storageRef.child("${uid}/${name}.png")
        //비트맵->바이트
        val baos =ByteArrayOutputStream()

        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)

        val imageData = baos.toByteArray()
        //이미지 업로드 to Storage
        var uploadTask = imgRef.putBytes(imageData)
        uploadTask.addOnFailureListener{
            Log.d("aaa", "failure....."+it)
        }.addOnCompleteListener{
            intent.putExtra("dataName",binding.addEditView.text.toString())
            intent.putExtra("dataColor",binding.colorSpinner.selectedItemPosition)
            intent.putExtra("dataType",binding.typeSpinner.selectedItemPosition)
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}

