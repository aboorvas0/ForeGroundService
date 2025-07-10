package com.example.pushnotification.Worker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.pushnotification.ForegroundService
import com.example.pushnotification.R
import kotlinx.coroutines.launch
import java.io.File


lateinit var button:Button
lateinit var stop_btn:Button
lateinit var workManager: WorkManager
lateinit var viewModel: MainViewModel
private lateinit var uncompressedImage: ImageView
private lateinit var compressedImage: ImageView
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        workManager=WorkManager.getInstance(applicationContext)
        viewModel=ViewModelProvider(this)[MainViewModel::class.java]
        uncompressedImage = findViewById(R.id.uncompressedImage)
        compressedImage = findViewById(R.id.compressedImage)
        handleIncomingImageIntent(intent)

        val uri: Uri? = viewModel.unCompressUri.value
        uri?.let {
            uncompressedImage.setImageURI(it)
        }

        // Example 2: Load Bitmap to compressedImage
//        val bitmap: Bitmap? = viewModel.bitMap.value
//        bitmap?.let {
//            compressedImage.setImageBitmap(it)
//        }

        lifecycleScope.launch {
            // Collect the UUID from ViewModel
            viewModel.uuid.collect { id ->
                if (id != null) {
                    workManager.getWorkInfoByIdLiveData(id)
                        .observe(this@MainActivity) { workInfo ->
                            if (workInfo != null) {
                                when (workInfo.state) {
                                    WorkInfo.State.SUCCEEDED -> {
                                        Log.d("Work", "Success ✅")

                                        // ✅ Get the file path from outputData
                                        val filePath = workInfo.outputData.getString(
                                            CompressWorker.KEY_RESULT
                                        )
                                        if (filePath != null) {
                                            val file = File(filePath)

                                            if (file.exists()) {
                                                  val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                                compressedImage.setImageBitmap(bitmap)
                                            } else {
                                                Log.e("BitmapError", "File does not exist at path: $filePath")
                                            }
                                            Log.d("Work", "Compressed file path: $filePath")

                                            // ✅ TODO: Use this file path (e.g., display image, upload, etc.)
                                        }
                                    }

                                    WorkInfo.State.FAILED -> {
                                        Log.d("Work", "Failed ❌")
                                    }

                                    else -> {
                                        Log.d("Work", "In Progress ⏳")
                                    }
                                }
                            }
                        }
                }
            }


        }




    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)


        handleIncomingImageIntent(intent)
    }
    private fun handleIncomingImageIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    ?: intent.clipData?.getItemAt(0)?.uri
            } else {
                intent.getParcelableExtra(Intent.EXTRA_STREAM)
                    ?: intent.clipData?.getItemAt(0)?.uri
            }

            uri?.let {
                Log.e(">>uri", it.toString())
                viewModel.unCompressUri(it)

                val request = OneTimeWorkRequestBuilder<CompressWorker>()
                    .setInputData(
                        workDataOf(
                            CompressWorker.KEY_CONTENT to it.toString(),
                            CompressWorker.KEY_COMPRESSION to 1024 * 20L
                        )
                    )
                    .setConstraints(Constraints(requiresStorageNotLow = true))
                    .build()

                workManager.enqueue(request)
                viewModel.updateUuid(request.id)
            }
        }
    }


}