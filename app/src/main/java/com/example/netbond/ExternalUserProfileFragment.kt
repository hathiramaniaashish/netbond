package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.example.netbond.controllers.FollowController
import com.example.netbond.models.User
import com.example.netbond.models.UserViewModel
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExternalUserProfileFragment : Fragment(R.layout.fragment_external_user_profile) {

    private val db = StorageService()
    private val followController = FollowController()
    private val viewModel: UserViewModel by activityViewModels()
    var userDocID:String? = null
    private var extUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            extUsername = bundle.getString("bundleKey")
            // Do something with the result
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserDocID()
        // Load data viewed of external user
        loadExternalUserData()
        // Set button for follow/requested/unfollow

        val btnFollow = view?.findViewById<Button>(R.id.btn_follow)
        setFollowStatus()
        btnFollow!!.setOnClickListener {
            updateFollowStatus()
        }
    }

    private fun setUserDocID() {
        viewModel.user.observe(viewLifecycleOwner) {
            userDocID = it.userDocID!!
        }
    }

    fun loadExternalUserData() {

        var imgProfile = view?.findViewById<ImageView>(R.id.img_profile)
        var txtName = view?.findViewById<TextView>(R.id.txt_name)
        var txtUsername = view?.findViewById<TextView>(R.id.txt_username)
        var txtnumFollowers = view?.findViewById<TextView>(R.id.txt_num_followers)
        var txtnumPoints = view?.findViewById<TextView>(R.id.txt_num_points)
        var txtnumFollowings = view?.findViewById<TextView>(R.id.txt_num_followings)

        CoroutineScope(Dispatchers.Main).launch{
            var extUser: User? = db.getUserByUsername(extUsername!!)
            Glide.with(requireView()).load(extUser!!.profile_image).into(imgProfile!!)
            txtName!!.text = extUser!!.name
            txtUsername!!.text = "@" + extUser!!.username
            txtnumFollowings!!.text = extUser!!.n_followings.toString()
            txtnumPoints!!.text = extUser!!.n_points.toString()
            txtnumFollowers!!.text = extUser!!.n_followers.toString()
        }
    }

    fun setFollowStatus() {
        val btnFollow = requireView().findViewById<Button>(R.id.btn_follow)
        CoroutineScope(Dispatchers.Main).launch {
            var thisUser = db.getUserByDocID(userDocID!!)
            var extUserDocID = db.getUserDocIDByUsername(extUsername!!)
            var extUser = db.getUserByDocID(extUserDocID)
            if (followController.ifThisRequestedToFollowExt(thisUser, extUser)) {
                btnFollow!!.text = "Pending"
            } else if (followController.ifThisFollowsExt(thisUser, extUser)) {
                btnFollow!!.text = "Unfollow"
            } else {
                btnFollow!!.text = "Follow"
            }
        }
    }

    fun updateFollowStatus() {
        val btnFollow = requireView().findViewById<Button>(R.id.btn_follow)
        CoroutineScope(Dispatchers.Main).launch {
            var thisUser = db.getUserByDocID(userDocID!!)
            var extUserDocID = db.getUserDocIDByUsername(extUsername!!)
            var extUser = db.getUserByDocID(extUserDocID)
            if (followController.ifThisFollowsExt(thisUser, extUser)) {
                btnFollow!!.text = "Follow"
                followController.thisUnfollowsExt(thisUser, extUser)
            } else if (followController.ifThisRequestedToFollowExt(thisUser, extUser)) {
                btnFollow!!.text = "Follow"
                followController.thisUnrequestsToFollowExt(thisUser, extUser)
            } else {
                btnFollow!!.text = "Pending"
                followController.thisRequestsToFollowExt(thisUser, extUser)
            }
        }
    }
}