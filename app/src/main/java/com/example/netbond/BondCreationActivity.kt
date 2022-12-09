package com.example.netbond

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.netbond.controllers.BondController
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BondCreationActivity : AppCompatActivity() {

    private val db = StorageService()
    private val bondController = BondController()
    private val currentUsername = "johndoe"// getExternalUsername()
    private var wrongList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bond_creation)

        // getUserData()

        setCreateBondBtn()

        val buttonClick = findViewById<Button>(R.id.btn_create_bond)
        buttonClick.setOnClickListener {
            // updateUserData()
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    fun setCreateBondBtn() {

        val btnAddWrongAns = findViewById<Button>(R.id.btn_add_wrong_ans)

        val editQuestion = findViewById<TextView>(R.id.edit_question)
        val editWrongAns = findViewById<TextView>(R.id.edit_wrong_ans)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        btnAddWrongAns.setOnClickListener {
            wrongList.add(editWrongAns.text.toString())
        }
    }

}