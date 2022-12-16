package com.example.netbond.controllers

import com.example.netbond.models.User
import com.example.netbond.services.StorageService

class FollowController {

    private val db = StorageService()

     suspend fun ifThisRequestedToFollowExt(thisUser: User?, extUser: User?): Boolean {
        return db.isThisRequestingToFollowExt(thisUser, extUser)
    }

    suspend fun ifThisFollowsExt(thisUser: User?, extUser: User?): Boolean {
        return db.isThisFollowingExt(thisUser, extUser)
    }

    fun thisUnfollowsExt(thisUser: User?, extUser: User?) {
        db.removeFollow(thisUser, extUser, true)
        db.removeFollow(extUser, thisUser, false)
        thisUser!!.n_followings?.plus(-1)
        extUser!!.n_followers?.plus(-1)
        db.updateUser(thisUser)
        db.updateUser(extUser)
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