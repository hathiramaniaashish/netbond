package com.example.netbond.services

import com.example.netbond.models.Bond
import com.example.netbond.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StorageService {

    private val db = FirebaseFirestore.getInstance()
    private val collUsers = db.collection("users")
    private val collBonds = db.collection("bonds")
    // private val auth = Firebase.auth

    suspend fun getUserDocIDByDocUserName(username: String): String {
        var userDocID = collUsers
            .whereEqualTo("username", username)
            .get()
            .await()
            .single()
            .id

        return userDocID
    }

    suspend fun getUserByDocID(userDocID: String): User? {
        var user = collUsers
            .document(userDocID)
            .get()
            .await()
            .toObject<User>()

        user?.userDocID = userDocID
        return user
    }

    fun updateUser(user : User?) : Boolean {
        collUsers
            .document(user!!.userDocID!!)
            .update(
                mapOf(
                    // Add remaining fields / Pass Custom class?
                    "profile_image" to user!!.profile_image,
                    "name" to user!!.name,
                    "username" to user!!.username,
                    "n_followings" to user!!.n_followings!!,
                    "n_followers" to user!!.n_followers!!,
                    "n_points" to user!!.n_points!!,
                    "email" to user!!.email
                )
            )
        return true
    }

    suspend fun isThisRequestingToFollowExt(thisUser: User?, extUser: User?): Boolean {
        return collUsers
            .document(thisUser!!.userDocID!!)
            .collection("sentRequests")
            .document(extUser!!.username!!).get().await().exists()
    }

    fun addNewRequest(thisUser: User?, extUser: User?, isFromThisUser:Boolean) {
        var coll = "receivedRequests"
        if (isFromThisUser) {
            coll = "sentRequests"
        }
        if (thisUser != null && extUser != null) {
            collUsers
                .document(thisUser.userDocID.toString())
                .collection(coll)
                .document(extUser.username.toString())
                .set(hashMapOf<String, Any>("delete" to "this")) // update("timestamp", FieldValue.serverTimestamp())
                .addOnFailureListener{ e -> println(e)
                }
        }
    }

    fun removeRequest(thisUser: User?, extUser: User?, isFromThisUser:Boolean) {
        var coll = "receivedRequests"
        if (isFromThisUser) {
            coll = "sentRequests"
        }
        collUsers
            .document(thisUser!!.userDocID!!)
            .collection(coll)
            .document(extUser!!.username!!)
            .delete()
    }

    suspend fun isThisFollowingExt(thisUser: User?, extUser: User?): Boolean {
        return collUsers
            .document(thisUser!!.userDocID!!)
            .collection("followings")
            .document(extUser!!.username!!).get().await().exists()
    }

    fun removeFollow(thisUser: User?, extUser: User?, isFromThisUser: Boolean) {
        val coll = "followers"
        if (isFromThisUser) {
            val coll = "followings"
        }
        collUsers
            .document(thisUser!!.userDocID!!)
            .collection(coll)
            .document(extUser!!.username!!).delete()
    }

    fun createBond(bond: Bond) {
        CoroutineScope(Dispatchers.Main).launch {
            val bondId = collBonds
                .add(bond)
                .await()
                .id
            collUsers
                .whereEqualTo("username", bond.author).get().addOnSuccessListener { result ->
                    val doc = result.single()
                    collUsers
                        .document(doc.id)
                        .collection("bonds")
                        .document(bondId)
                        .set(hashMapOf("question" to bond.question))
                }
            collBonds
                .document(bondId)
                .collection("interactions")
                .document("initial")
                .set({})
        }
    }

    suspend fun getBondById(bondId: String): Bond? = collBonds
        .document(bondId)
        .get()
        .await()
        .toObject<Bond>()

}


















