package com.example.netbond.services

import android.view.View
import com.google.android.material.snackbar.Snackbar

class Utils {

    fun displayMessage(view: View, message: String) {
        var snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

}