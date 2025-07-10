package com.example.pushnotification.contact

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pushnotification.R
import com.example.pushnotification.contentProvider.ContactAdapter
import com.example.pushnotification.contentProvider.ContentViewModel
import com.example.pushnotification.contentProvider.ImageAdapter

private lateinit var viewModel: ContactViewModel
private lateinit var contactAdapter: ContactAdapter
class ContactActivity : AppCompatActivity() {
    val requestContactPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            loadContacts()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact)
        viewModel=ViewModelProvider(this)[ContactViewModel::class.java]
        requestContactPermission.launch(Manifest.permission.READ_CONTACTS)





        val recyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView)
        contactAdapter = ContactAdapter(this)
        recyclerView.adapter = contactAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launchWhenStarted {
           viewModel.listContact.collect(){
               list->
               contactAdapter.submitList(list)
           }
        }


    }
    private fun loadContacts() {
        val contactList= mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        )?.use {
            cursor->
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()){
                val name=cursor.getString(nameIndex)
                val number=cursor.getString(numberIndex)
                contactList.add(Contact(name,number))
            }
            viewModel.updateList(contactList)

        }
    }


}

data class Contact(

    val name:String,
   val number:String
)