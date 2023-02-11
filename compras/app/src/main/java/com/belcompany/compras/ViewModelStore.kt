package com.belcompany.compras

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.belcompany.compras.data.Element

class ViewModelStore: ViewModel() {
    val myObject = MutableLiveData<Element>()

    fun updateData(newValue: Element) {
        myObject.postValue(newValue)
    }
}