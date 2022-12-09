package com.example.netbond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountSettingActivity : AppCompatActivity() {

    private val db = StorageService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        getUserData()

        val buttonClick = findViewById<Button>(R.id.btn_save_settings)
        buttonClick.setOnClickListener {
            updateUserData()
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUserData() {

        val thisUsername = "johndoe"// getThisUsername()

        // El botón debería tener la imagen como fondo
        // val imgProfile = findViewById<ImageView>(R.id.img_profile)
        val editName = findViewById<TextView>(R.id.edit_name)
        val editUsername = findViewById<TextView>(R.id.edit_username)
        val editEmail = findViewById<TextView>(R.id.edit_email)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUser(thisUsername)

            editUsername.text = thisUsername
            if (user != null) {
                editName.text = user.name
                editEmail.text = user.email
            }
        }
    }


    private fun updateUserData() {

        val thisUsername = "johndoe"// getThisUsername()

        // El botón debería tener la imagen como fondo
        // val imgProfile = findViewById<ImageView>(R.id.img_profile)
        val editName = findViewById<TextView>(R.id.edit_name)
        val editUsername = findViewById<TextView>(R.id.edit_username)
        val editEmail = findViewById<TextView>(R.id.edit_email)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUser(thisUsername)

            if (user != null) {
                user.name = editName.text.toString()
                user.username = editUsername.text.toString()
            }
            db.updateUser(user)
        }

    }
}