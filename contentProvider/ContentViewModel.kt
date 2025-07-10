package com.example.pushnotification.contentProvider

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContentViewModel:ViewModel() {
    private var _listImage = MutableStateFlow<List<ContentProviderActivity.Image>>(emptyList())
    var listImage: StateFlow<List<ContentProviderActivity.Image>> = _listImage


    fun updateList(list: List<ContentProviderActivity.Image>){
        _listImage.value=list
    }

}