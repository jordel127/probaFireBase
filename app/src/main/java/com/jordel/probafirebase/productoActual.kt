package com.jordel.probafirebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class productoActual : ViewModel() {

    private val _producte = MutableLiveData<Producte>()
    val producte: LiveData<Producte>
        get() = _producte

    fun setProducte(producte: Producte) {
        _producte.value = producte
    }
}