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
import com.example.pushnotification.contact.Contact
import java.io.ByteArrayOutputStream

class ContactAdapter(val context:Context): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    var list= mutableListOf<Contact>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactAdapter.ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }


    override fun onBindViewHolder(holder: ContactAdapter.ContactViewHolder, position: Int) {
        var itemView=list[position]
        holder.nameView.text=itemView.name
        holder. numberView.text=itemView.number
    }



    fun submitList(newList:List<Contact>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ContactViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val nameView: TextView = itemView.findViewById(R.id.name)
        val numberView: TextView = itemView.findViewById(R.id.number)

    }
}