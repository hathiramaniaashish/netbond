package com.example.netbond.services

import com.google.firebase.auth.FirebaseAuth

class AuthService {

   private val auth: FirebaseAuth = FirebaseAuth.getInstance()

   fun checkUserSignedIn(): String? {
      val user = auth.currentUser
      return user?.email
   }

}