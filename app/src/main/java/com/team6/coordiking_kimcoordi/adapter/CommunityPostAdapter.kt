package com.team6.coordiking_kimcoordi.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.activity.MyApplication
import kotlinx.android.synthetic.main.item_gallery_image.view.*

class CommunityPostAdapter(private val itemList: List<Post>): RecyclerView.Adapter<CommunityPostAdapter.ViewHolder>() {
    private var context: Context? = null
    var listener: GalleryImageClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommunityPostAdapter.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_gallery_image, parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityPostAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val image = itemList.get(adapterPosition)

            //storage 이미지 다운로드
            val imgRef= MyApplication.storage
                .reference
                .child("${MyApplication.user!!.uid}/${image.dataName}.png").downloadUrl.addOnSuccessListener {  }.addOnSuccessListener {
                    // load image
                    Glide.with(context!!).load(it).into(itemView.ivGalleryImage)
                }.addOnCanceledListener {
                    Log.d("kim","failed to download")
                }

            // adding click or tap handler for our image layout
            itemView.container.setOnClickListener {
                listener?.onClick(adapterPosition)
            }
        }
    }
}