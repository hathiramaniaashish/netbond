package com.example.netbond.services

import com.example.netbond.models.Bond
import com.example.netbond.models.User
import com.google.firebase.firestore.FieldValue
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

    suspend fun getUsernameByUserDocID(userDocID: String): String {
        var user = getUserByDocID(userDocID)

        return user.username!!
    }

    suspend fun getUserDocIDByUsername(username: String): String {
        var userDocID = collUsers
            .whereEqualTo("username", username)
            .get()
            .await()
            .single()
            .id

        return userDocID
    }

    suspend fun getUserByDocID(userDocID: String): User {
        var user = collUsers
            .document(userDocID)
            .get()
            .await()
            .toObject<User>()

        user?.userDocID = userDocID
        return user!!
    }

    suspend fun getUserByEmail(email: String): User {
        val user = collUsers
            .whereEqualTo("email", email)
            .get()
            .await()
            .single()
            .toObject<User>()

        user.userDocID = getUserDocIDByUsername(user.username!!)
        return user
    }

    suspend fun getUserByUsername(username: String): User {
        var user = collUsers
            .whereEqualTo("username", username)
            .get()
            .await()
            .single()
            .toObject<User>()

        user?.userDocID = getUserDocIDByUsername(username)
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
        var coll = "followers"
        if (isFromThisUser) {
            coll = "followings"
        }
        collUsers
            .document(thisUser!!.userDocID!!)
            .collection(coll)
            .document(extUser!!.username!!).delete()
    }

    fun createBond(userDocID:String, bond: Bond) {
        CoroutineScope(Dispatchers.Main).launch {
            val bondId = collBonds
                .add(bond)
                .await()
                .id
            collUsers
                .document(userDocID)
                .collection("bonds")
                .document(bondId)
                .set(hashMapOf("question" to bond.question))
        }
    }

    suspend fun getBondById(bondId: String): Bond? = collBonds
        .document(bondId)
        .get()
        .await()
        .toObject<Bond>()

    fun deleteBondByBondID(userDocID:String, bondID: String) {
        CoroutineScope(Dispatchers.Main).launch {
            collBonds
                .document(bondID)
                .delete()
            collUsers
                .document(userDocID)
                .collection("bonds")
                .document(bondID)
                .delete()
        }
    }


    //-------------------------
    // AASHISH
    //-------------------------

    suspend fun existsUser(username: String): Boolean {
        val users = collUsers.get().await()
        users.forEach { user ->
            val actualUsername = user.get("username").toString()
            if (actualUsername == username) return true
        }
        return false
    }

    suspend fun existsEmail(email: String): Boolean {
        val users = collUsers.get().await()
        users.forEach { user ->
            val oldEmail = user.get("email").toString()
            if (oldEmail == email) return true
        }
        return false
    }

    fun addNewUser(user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            collUsers.add(user)
        }
    }

    suspend fun getUsers(): MutableList<User> {
        val usersList = emptyList<User>().toMutableList()
        val users = collUsers.get().await()
        users.forEach { user ->
            usersList.add(user.toObject())
        }
        return usersList
    }

    suspend fun getBondByID(bondID: String): Bond {
        return collBonds.document(bondID).get().await().toObject<Bond>()!!
    }

    suspend fun hasInteracted(username: String, bondID: String): Boolean {
        val interactions = collBonds.document(bondID)
            .collection("interactions").get().await()
        interactions.forEach { interaction ->
            if (interaction.id == username) return true
        }
        return false
    }

    suspend fun getUserBondsID(username: String): MutableList<String> {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        val userBondsList = emptyList<String>().toMutableList()
        val userBonds = collUsers.document(userDoc.id)
            .collection("bonds").get().await()
        userBonds.forEach { bond ->
            userBondsList.add(bond.id)
        }
        return userBondsList
    }

    suspend fun getReceivedRequests(username: String): MutableList<User> {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        val requestsList = emptyList<User>().toMutableList()
        val requests = collUsers.document(userDoc.id)
            .collection("receivedRequests").get().await()
        requests.forEach { request ->
            requestsList.add(getUserByUsername(request.id))
        }
        return requestsList
    }

    suspend fun getFollowings(username: String): MutableList<User> {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        val followingsList = emptyList<User>().toMutableList()
        val followings = collUsers.document(userDoc.id)
            .collection("followings").get().await()
        followings.forEach { following ->
            followingsList.add(getUserByUsername(following.id))
        }
        return followingsList
    }

    suspend fun deleteReceivedRequest(username: String, request: String) {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        collUsers.document(userDoc.id)
            .collection("receivedRequests").document(request).delete().await()
    }

    suspend fun deleteSentRequest(username: String, request: String) {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        collUsers.document(userDoc.id)
            .collection("sentRequests").document(request).delete().await()
    }

    suspend fun addInteraction(bondID: String, interaction: String) {
        collBonds.document(bondID)
            .collection("interactions")
            .document(interaction).set(emptyMap<String, String>()).await()
    }

    suspend fun addFollower(username: String, follower: String) {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        collUsers.document(userDoc.id)
            .collection("followers")
            .document(follower).set(emptyMap<String, String>()).await()
    }

    suspend fun addFollowing(username: String, following: String) {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        collUsers.document(userDoc.id)
            .collection("followings")
            .document(following).set(emptyMap<String, String>()).await()
    }

    suspend fun incrementFollowers(username: String, number: Long) {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        collUsers.document(userDoc.id)
            .update("n_followers", FieldValue.increment(number)).await()
    }

    suspend fun incrementFollowings(username: String, number: Long) {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        collUsers.document(userDoc.id)
            .update("n_followings", FieldValue.increment(number)).await()
    }

    suspend fun incrementPoints(username: String, number: Long) {
        val userDoc = collUsers.whereEqualTo("username", username).get().await().single()
        collUsers.document(userDoc.id)
            .update("n_points", FieldValue.increment(number)).await()
    }
}


















