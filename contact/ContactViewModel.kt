package com.example.pushnotification.contact

import androidx.lifecycle.ViewModel
import com.example.pushnotification.contentProvider.ContentProviderActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContactViewModel:ViewModel() {
    private var _listContact = MutableStateFlow<List<Contact>>(emptyList())
    var listContact: StateFlow<List<Contact>> = _listContact

    fun updateList(list: List<Contact>){
        _listContact.value=list
    }
}