package com.example.pushnotification.Worker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream

class CompressWorker(
   private val context: Context,
   private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val stringUri = params.inputData.getString(KEY_CONTENT)
            val compressionBytes = params.inputData.getLong(
                KEY_COMPRESSION,
                0L
            )
            val uri = Uri.parse(stringUri)
            val bytes = applicationContext.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: return@withContext Result.failure()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            var outputBytes: ByteArray
            var quality = 100
            do {
                val outputStream = ByteArrayOutputStream()
                outputStream.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    outputBytes = outputStream.toByteArray()
                    quality -= (quality * 0.1).toInt()
                }
            } while (outputBytes.size > compressionBytes && quality>5)
            val file=File(applicationContext.cacheDir,"${params.id}.jpg")
            file.writeBytes(outputBytes)
            Result.success(
                workDataOf(
                    KEY_RESULT to file.absolutePath
                )
            )


        }


    }
    companion object{
        const val KEY_CONTENT="Key_content"
        const val KEY_COMPRESSION="Key_Compression"
        const val KEY_RESULT="Key_Result"
    }
}