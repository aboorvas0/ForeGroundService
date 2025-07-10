package com.example.pushnotification.contentProvider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.pushnotification.R
import java.io.ByteArrayOutputStream

class ImageAdapter(val context:Context): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var list= mutableListOf<ContentProviderActivity.Image>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageAdapter.ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }


    override fun onBindViewHolder(holder: ImageAdapter.ImageViewHolder, position: Int) {
            var itemView=list[position]
            holder.nameView.text=itemView.name
            var bitmap= getBitMap(itemView.uri)
            holder.imageView.setImageBitmap(bitmap)
    }

    private fun getBitMap(uri: Uri?):Bitmap? {
        val inputStream=uri?.let { context.contentResolver.openInputStream(it) }
        if(inputStream!=null){
            val orginalBitmap= BitmapFactory.decodeStream(inputStream)
            val outputByteArray= ByteArrayOutputStream()
            orginalBitmap.compress(Bitmap.CompressFormat.JPEG,40,outputByteArray)
            return BitmapFactory.decodeByteArray(outputByteArray.toByteArray(),0,outputByteArray.size())
        }else{
            Log.e("BitmapError", "Failed to decode bitmap from URI: $uri")
            return null
        }
    }

    fun submitList(newList:List<ContentProviderActivity.Image>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
           return list.size
    }
    inner class ImageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val nameView: TextView = itemView.findViewById(R.id.imageName)

    }
}