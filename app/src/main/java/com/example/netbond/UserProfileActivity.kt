package com.example.netbond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.netbond.models.User
import com.example.netbond.services.StorageService
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {

    val db = StorageService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        getUserData()

        val buttonClick = findViewById<Button>(R.id.btn_account_settings)
        buttonClick.setOnClickListener {
            val intent = Intent(this, AccountSettingActivity::class.java)
            startActivity(intent)
        }
    }


    private fun getUserData() {

        var imgProfile = findViewById<ImageView>(R.id.img_profile)
        var txtName = findViewById<TextView>(R.id.txt_name)
        var txtUsername = findViewById<TextView>(R.id.txt_username)
        var txtnumFollowers = findViewById<TextView>(R.id.txt_num_followers)
        var txtnumPoints = findViewById<TextView>(R.id.txt_num_points)
        var txtnumFollowings = findViewById<TextView>(R.id.txt_num_followings)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        var thisUsername = "johndoe"// getThisUsername()
        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUser(thisUsername)
            txtUsername.text = thisUsername
            if (user != null) {
                txtName.text = user.name
                txtnumFollowings.text = user.n_followings.toString()
                txtnumPoints.text = user.n_points.toString()
                txtnumFollowers.text = user.n_followers.toString()
            }
        }

    }

//    private fun getName() {
//        // Get the decimal value from the cost of service text field
//        val stringInTextField = binding.editxt_name.text.toString()
//        val cost = stringInTextField.toDoubleOrNull()
//    }
}