package com.example.netbond.controllers

import com.example.netbond.models.Bond
import com.example.netbond.services.StorageService

class BondController {

    private val db = StorageService()

    fun shareBond(username:String, question: String, ansList: HashMap<String, String>, keyRight: String) {
        var bond = Bond(username, question, ansList, keyRight)
        db.createBond(bond)
    }

    suspend fun getBondById(bondId: String): Bond? {
        return db.getBondById(bondId)
    }

}