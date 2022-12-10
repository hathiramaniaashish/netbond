package com.example.netbond.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthService {

   private val auth: FirebaseAuth = FirebaseAuth.getInstance()

   fun checkUserSignedIn(): Boolean {
      if (auth.currentUser != null) {
         return true
      }
      return false
   }

   fun signUp(name: String, email: String, username: String, password: String): FirebaseUser? {
      var user: FirebaseUser? = null;
      auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
         if (it.isSuccessful) {
            user = auth.currentUser
         }
      }
      return user
   }

   fun logIn(email: String, password: String): FirebaseUser? {
      auth.signInWithEmailAndPassword(email, password)
      return auth.currentUser
   }

}