package com.example.netbond.services

import android.content.Context
import android.widget.Toast

class Utils {

    fun displayMessage(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.show()
    }

}