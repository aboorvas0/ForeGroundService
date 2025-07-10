package com.example.pushnotification.uri

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pushnotification.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private lateinit var compressedImage: ImageView


class UriActivity : AppCompatActivity() {
    //image picker for normal getContent
    private val getImage=registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        val compressImage=compressUri(uri)
        compressImage?.let { compressedImage.setImageBitmap(it) }

    }


    //image picker for PickVisualMedia
    private val pickMedia=registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
        uri->
        val compressImage=compressUri(uri)
        compressImage?.let { compressedImage.setImageBitmap(it) }

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_uri)
       compressedImage = findViewById(R.id.compressedImage)
        handleIncomingImageIntent(intent)


        //normal image picker
        compressedImage.setOnClickListener{
            //30+ above
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                //PickVisualMedia
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            }else{
                //getcontent
                getImage.launch("image/*")
            }

        }


    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIncomingImageIntent(intent)

    }
    private fun compressUri(uri: Uri?): Bitmap? {
        val inputStream=uri?.let { contentResolver.openInputStream(it) }
        if(inputStream!=null){
            val orginalBitmap=BitmapFactory.decodeStream(inputStream)
            val outputByteArray=ByteArrayOutputStream()
            orginalBitmap.compress(Bitmap.CompressFormat.JPEG,40,outputByteArray)
            return BitmapFactory.decodeByteArray(outputByteArray.toByteArray(),0,outputByteArray.size())
        }else{
            Log.e("BitmapError", "Failed to decode bitmap from URI: $uri")
            return null
        }
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

            println(uri)
            //contentResolver to access the image from uri
                //openInputStream to read a uri image
            //read a bytes for store in file and display using uri
            val inputStream= uri?.let { contentResolver.openInputStream(it) }.use {
                it?.readBytes()
            }
            val file=File(filesDir,"temp_img.jpg")
            FileOutputStream(file).use {
                it.write(inputStream)
            }

            println(file.toUri())
            // output:file:///data/user/0/com.example.pushnotification/files/temp_img.jpg

            /* absolutePath
            Returns the complete path (from root) to the file on the device — as a String.
            /data/user/0/com.example.myapp/cache/image.jpg
            file.path	        The path you passed ("cache/image.jpg")
            file.absolutePath	The full resolved path on the device*/

            val bitmap= BitmapFactory.decodeFile(file.absolutePath)
            compressedImage.setImageBitmap(bitmap)


            /*
            1.filesDir
            🔹 Meaning:
            context.filesDir points to the internal storage directory used to store app-specific files.

            Path example:


              /data/user/0/com.example.myapp/files/

            ✅ Characteristics:
            Persistent — the files stay there until you delete them or the app is uninstalled.

            Ideal for saving user data or app configs that you want to keep long-term.

            Private to your app (not accessible by others).

            🔸 Example:

            val file = File(context.filesDir, "mydata.txt")

            ⚡ 2. cacheDir
            🔹 Meaning:
            context.cacheDir points to the internal cache directory used to store temporary data.

            Path example:


            /data/user/0/com.example.myapp/cache/

            ✅ Characteristics:
            Temporary — the system may delete these files when low on storage.

            Good for images, temporary files, downloaded data that you can recreate later.

            Also private to your app.

            🔸 Example:

            val file = File(context.cacheDir, "temp_image.jpg")

            | Feature        | `filesDir`                         | `cacheDir`                           |
| -------------- | ---------------------------------- | ------------------------------------ |
| Location       | `/files/`                          | `/cache/`                            |
| Use case       | Save files to **keep permanently** | Save files to **delete later**       |
| Auto-cleaned?  | ❌ No                               | ✅ Yes (by system when needed)        |
| Private to app | ✅ Yes                              | ✅ Yes                                |
| Needs cleanup? | ✅ You delete manually              | ✅ You should clean manually if large |


| Scenario                          | Use        |
| --------------------------------- | ---------- |
| Save user settings or logs        | `filesDir` |
| Save temporary compressed image   | `cacheDir` |
| Store downloaded PDFs temporarily | `cacheDir` |
| Export file that should persist   | `filesDir` |



        */



        }

    }
}