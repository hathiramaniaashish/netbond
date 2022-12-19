package com.example.netbond.controllers

import com.example.netbond.models.Bond
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BondController {

    private val db = StorageService()

    fun shareBond(userDocID:String, question: String, ansList: HashMap<String, String>, keyRight: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val username = db.getUserByDocID(userDocID).username
            var bond = Bond(username, question, ansList, keyRight)
            db.createBond(userDocID, bond)
        }
    }

    suspend fun getBondById(bondId: String): Bond? {
        return db.getBondById(bondId)
    }

}