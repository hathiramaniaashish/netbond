package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.netbond.models.UserViewModel
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    val db = StorageService()
    private val viewModel: UserViewModel by activityViewModels()
    var userDocID:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDocID = viewModel.userDocID!!
        getUserData()

        val buttonClick = view.findViewById<Button>(R.id.btn_account_settings)
        buttonClick.setOnClickListener {
            // Action
            findNavController().navigate(R.id.accountSettingFragment)
        }
    }


    private fun getUserData() {

        var imgProfile = view?.findViewById<ImageView>(R.id.img_profile)
        var txtName = view?.findViewById<TextView>(R.id.txt_name)
        var txtUsername = view?.findViewById<TextView>(R.id.txt_username)
        var txtnumFollowers = view?.findViewById<TextView>(R.id.txt_num_followers)
        var txtnumPoints = view?.findViewById<TextView>(R.id.txt_num_points)
        var txtnumFollowings = view?.findViewById<TextView>(R.id.txt_num_followings)

        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUserByDocID(userDocID!!)
            if ((user != null) && (imgProfile != null)) {
                txtUsername!!.text = "@" + user!!.username
                Glide.with(requireView()).load(user.profile_image).into(imgProfile)
                txtName!!.text = user.name
                txtnumFollowings!!.text = user.n_followings.toString()
                txtnumPoints!!.text = user.n_points.toString()
                txtnumFollowers!!.text = user.n_followers.toString()
            }
        }

    }

}