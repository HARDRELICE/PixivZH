package com.hardrelice.pixivzh.ui.main.frg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SavedViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is saved Fragment"
    }
    val text: LiveData<String> = _text
}