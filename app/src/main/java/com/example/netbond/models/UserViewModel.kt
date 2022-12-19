package com.example.netbond.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {

    val user = MutableLiveData<User>()

    fun setUser(newUser: User) {
        user.value = newUser
    }

}