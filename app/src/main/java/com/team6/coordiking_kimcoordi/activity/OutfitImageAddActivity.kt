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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.StorageReference
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.databinding.ActivityImageAddBinding
import com.team6.coordiking_kimcoordi.databinding.ActivityOutfitImageAddBinding
import java.io.ByteArrayOutputStream

class OutfitImageAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityOutfitImageAddBinding
    lateinit var bitmap: Bitmap
    lateinit var colorSpinner: Spinner
    lateinit var styleSpinner: Spinner
    lateinit var check: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //바인딩 초기화
        binding= ActivityOutfitImageAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // colorSpinner Init
        colorSpinner = binding.spnColor
        colorSpinner.adapter = ArrayAdapter.createFromResource(this,R.array.color_picker_array, android.R.layout.simple_spinner_item)

        //styleSpinner init
        styleSpinner = binding.spnStyle
        styleSpinner.adapter = ArrayAdapter.createFromResource(this,R.array.style_picker_array, android.R.layout.simple_spinner_item)

        //checkBox
        check = binding.chkFavourite

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
                .into(binding.ivAddOutfit)

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
            if(binding.ivAddOutfit.drawable !== null && binding.etAddOutfit.text.isNotEmpty()){
                // 이미지 storage 업로드
                uploadImage(MyApplication.user!!.uid, binding.etAddOutfit.text.toString(), check.isChecked)
                Toast.makeText(this, "데이터가 저장되었습니다", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadImage(uid:String, name: String, fav: Boolean){
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
            Log.d("outfitadd", "ERR: $it")
        }.addOnCompleteListener{
            intent.putExtra("dataName",binding.etAddOutfit.text.toString())
            intent.putExtra("dataColor",binding.spnColor.selectedItemPosition)
            intent.putExtra("dataStyle",binding.spnStyle.selectedItemPosition)
            intent.putExtra("favourite",fav)
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}