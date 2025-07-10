package com.example.pushnotification.Worker

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class MainViewModel:ViewModel() {
    private var _unCompressUri = MutableStateFlow<Uri?>(null)
    var unCompressUri: StateFlow<Uri?> = _unCompressUri

    private var _bitmap = MutableStateFlow<Bitmap?>(null)
    var bitMap: StateFlow<Bitmap?> = _bitmap

    private var _uuid = MutableStateFlow<UUID?>(null)
    var uuid: StateFlow<UUID?> = _uuid


    fun unCompressUri(uri: Uri?){
       _unCompressUri.value=uri
    }
    fun updateBitmap(bitmap: Bitmap?){
        _bitmap.value=bitmap
    }
fun updateUuid(uuid: UUID?){
    _uuid.value=uuid
}

}