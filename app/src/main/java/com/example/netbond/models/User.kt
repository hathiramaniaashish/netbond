package com.example.netbond.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User (
    var profile_image: String? = null,
    var name: String? = null,
    var username: String? = null,
    var n_followings: Int? = null,
    var n_points: Int? = null,
    var n_followers: Int? = null,
    var email: String? = null,

    @get:Exclude
    var userDocID: String? = null
)