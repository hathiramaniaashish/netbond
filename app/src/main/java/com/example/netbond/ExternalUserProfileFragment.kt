package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.netbond.controllers.FollowController
import com.example.netbond.models.User
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExternalUserProfileFragment : Fragment(R.layout.fragment_external_user_profile) {

    private val db = StorageService()
    private val followController = FollowController()
    private val currentUsername = "johndoe"// getExternalUsername()
    private val externalUsername = "aashish"// getExternalUsername()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.activity_external_user_profile, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load data viewed of external user
        loadExternalUserData()
        // Set button for follow/requested/unfollow
        setFollowBtn()
    }

    fun loadExternalUserData() {

        var imgProfile = view?.findViewById<ImageView>(R.id.img_profile)
        var txtName = view?.findViewById<TextView>(R.id.txt_name)
        var txtUsername = view?.findViewById<TextView>(R.id.txt_username)
        var txtnumFollowers = view?.findViewById<TextView>(R.id.txt_num_followers)
        var txtnumPoints = view?.findViewById<TextView>(R.id.txt_num_points)
        var txtnumFollowings = view?.findViewById<TextView>(R.id.txt_num_followings)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)
        CoroutineScope(Dispatchers.Main).launch{
            var extUser: User? = db.getUser(externalUsername)
            Glide.with(requireView()).load(extUser!!.profile_image).into(imgProfile!!)
            txtName!!.text = extUser!!.name
            txtUsername!!.text = extUser!!.username
            txtnumFollowings!!.text = extUser!!.n_followings.toString()
            txtnumPoints!!.text = extUser!!.n_points.toString()
            txtnumFollowers!!.text = extUser!!.n_followers.toString()
        }
    }

    fun setFollowBtn() {

//        val intent = Intent(this, ExternalUserProfileActivity::class.java)
        val btnFollow = view?.findViewById<Button>(R.id.btn_follow)
        CoroutineScope(Dispatchers.Main).launch {
            var thisUser = db.getUser(currentUsername)
            var extUser = db.getUser(externalUsername)
            if (followController.ifThisRequestedToFollowExt(thisUser, extUser)) {
                btnFollow!!.text = "Pending"
                btnFollow!!.setOnClickListener {
                    btnFollow!!.text = "Follow"
                    followController.thisUnrequestsToFollowExt(thisUser, extUser)
//                    startActivity(intent)
                }
            } else if (followController.ifThisFollowsExt(thisUser, extUser)) {
                btnFollow!!.text = "Unfollow"
                btnFollow!!.setOnClickListener {
                    btnFollow!!.text = "Follow"
                    followController.thisUnfollowsExt(thisUser, extUser)
//                    startActivity(intent)
                }
            } else {
                btnFollow!!.text = "Follow"
                btnFollow!!.setOnClickListener {
                    btnFollow!!.text = "Pending"
                    followController.thisRequestsToFollowExt(thisUser, extUser)
//                    startActivity(intent)
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