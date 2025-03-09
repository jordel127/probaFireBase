package com.jordel.probafirebase

import androidx.lifecycle.ViewModel

class VMuser: ViewModel() {

    private var _user = ""
    val user: String
        get() = _user

    fun setUser(user: String){
        _user = user
    }
}