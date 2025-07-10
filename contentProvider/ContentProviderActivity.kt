package com.example.pushnotification.contentProvider

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pushnotification.R
import java.util.Calendar

private lateinit var viewModel:ContentViewModel
private lateinit var adapter: ImageAdapter
class ContentProviderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_content_provider)

        viewModel=ViewModelProvider(this)[ContentViewModel::class.java]

        val projection= arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val yesterday=Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR,-30)
        }.timeInMillis

        val selection= "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        val selectionArgs= arrayOf(yesterday.toString())

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use {

            cursor->
            val list= mutableListOf<Image>()
            val idColumns=cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumns=cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()){
                val id=cursor.getLong(idColumns)
                val name=cursor.getString(nameColumns)
                val uri=ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id)
                list.add(Image(id,name,uri))
                viewModel.updateList(list)
            }
        }



        val recyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView)
        adapter = ImageAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launchWhenStarted {
            viewModel.listImage.collect { list ->
                adapter.submitList(list)
            }
        }

    }
    data class Image(
        val id:Long,
        val name:String,
        val uri:Uri
    )
}