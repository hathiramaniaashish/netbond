package com.example.netbond.models

data class Bond(
    var author: String? = null,
    var question: String? = null,
    var ansList: HashMap<String, String>? = null,
    var keyRight: String? = null
)
