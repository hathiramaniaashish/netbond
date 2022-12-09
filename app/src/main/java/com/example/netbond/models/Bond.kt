package com.example.netbond.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Bond(
    var author: String? = null,
    var question: String? = null,
    var ansList: HashMap<String, String>,
    var keyRight:String? = null
)
