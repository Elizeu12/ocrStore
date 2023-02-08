package com.belcompany.compras

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.belcompany.compras.data.Element

class ViewModelStore: ViewModel() {
    val myObject = MutableLiveData<Element>()

    fun setData(element: Element) {
        myObject.value = Element(element.title,element.price)
    }
}