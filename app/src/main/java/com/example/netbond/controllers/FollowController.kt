package com.example.netbond.controllers

import com.example.netbond.models.User
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowController {

    private val db = StorageService()

     suspend fun ifThisRequestedToFollowExt(thisUser: User?, extUser: User?): Boolean {
        return db.isThisRequestingToFollowExt(thisUser, extUser)
    }

    suspend fun ifThisFollowsExt(thisUser: User?, extUser: User?): Boolean {
        return db.isThisFollowingExt(thisUser, extUser)
    }

    fun thisUnfollowsExt(thisUser: User?, extUser: User?) {
        CoroutineScope(Dispatchers.Main).launch {
            db.removeFollow(thisUser, extUser, true)
            db.removeFollow(extUser, thisUser, false)
//            db.incrementFollowers(extUser!!.username!!, -2)
//            db.incrementFollowings(thisUser!!.username!!, -2)
//            db.incrementPoints(thisUser!!.username!!, -2)
//            println(thisUser!!.n_followers!!)
//            thisUser!!.n_followers!!.dec()
//            println(thisUser!!.n_followers!!)
//            extUser!!.n_followers!!.dec()
            db.incrementFollowings(thisUser!!.username!!, -1)
            db.incrementFollowers(extUser!!.username!!, -1)
        }
    }

    fun thisUnrequestsToFollowExt(thisUser: User?, extUser: User?) {
        db.removeRequest(thisUser, extUser, true)
        db.removeRequest(extUser, thisUser, false)
    }

    fun thisRequestsToFollowExt(thisUser: User?, extUser: User?) {
        db.addNewRequest(thisUser, extUser, true)
        db.addNewRequest(extUser, thisUser, false)
    }

}