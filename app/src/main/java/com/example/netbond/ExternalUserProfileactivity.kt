package com.example.netbond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.netbond.controllers.FollowController
import com.example.netbond.models.User
import com.example.netbond.services.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExternalUserProfileActivity : AppCompatActivity() {

    private val db = StorageService()
    private val followController = FollowController()
    private val currentUsername = "johndoe"// getExternalUsername()
    private val externalUsername = "ash"// getExternalUsername()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_external_user_profile)

        // Load data viewed of external user
        loadExternalUserData()
        // Set button for follow/requested/unfollow
        setFollowBtn()
    }

    fun loadExternalUserData() {

        var imgProfile = findViewById<ImageView>(R.id.img_profile)
        var txtName = findViewById<TextView>(R.id.txt_name)
        var txtUsername = findViewById<TextView>(R.id.txt_username)
        var txtnumFollowers = findViewById<TextView>(R.id.txt_num_followers)
        var txtnumPoints = findViewById<TextView>(R.id.txt_num_points)
        var txtnumFollowings = findViewById<TextView>(R.id.txt_num_followings)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)
        CoroutineScope(Dispatchers.Main).launch{
            var thisUser: User? = db.getUser(currentUsername)
            var extUser: User? = db.getUser(externalUsername)
            txtName.text = extUser!!.name
            txtUsername.text = extUser!!.username
            txtnumFollowings.text = extUser!!.n_followings.toString()
            txtnumPoints.text = extUser!!.n_points.toString()
            txtnumFollowers.text = extUser!!.n_followers.toString()
        }
    }

    fun setFollowBtn() {

        val intent = Intent(this, ExternalUserProfileActivity::class.java)
        val btnFollow = findViewById<Button>(R.id.btn_follow)
        CoroutineScope(Dispatchers.Main).launch {
            var thisUser = db.getUser(currentUsername)
            var extUser = db.getUser(externalUsername)
            if (followController.ifThisRequestedToFollowExt(thisUser, extUser)) {
                btnFollow.text = "Pending"
                btnFollow.setOnClickListener {
                    btnFollow.text = "Follow"
                    followController.thisUnrequestsToFollowExt(thisUser, extUser)
                    startActivity(intent)
                }
            } else if (followController.ifThisFollowsExt(thisUser, extUser)) {
                btnFollow.text = "Unfollow"
                btnFollow.setOnClickListener {
                    btnFollow.text = "Follow"
                    followController.thisUnfollowsExt(thisUser, extUser)
                    startActivity(intent)
                }
            } else {
                btnFollow.text = "Follow"
                btnFollow.setOnClickListener {
                    btnFollow.text = "Pending"
                    followController.thisRequestsToFollowExt(thisUser, extUser)
                    startActivity(intent)
                }
            }
        }
    }

//    private fun getName() {
//        // Get the decimal value from the cost of service text field
//        val stringInTextField = binding.editxt_name.text.toString()
//        val cost = stringInTextField.toDoubleOrNull()
//    }
}