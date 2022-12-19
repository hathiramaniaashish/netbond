package com.example.netbond

import android.os.Bundle
import androidx.core.os.bundleOf
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.netbond.databinding.AcceptUserTemplateBinding
import com.example.netbond.databinding.FragmentUserSearchBarBinding
import com.example.netbond.databinding.SearchUserTemplateBinding
import com.example.netbond.services.StorageService
import com.example.netbond.services.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.fragment.app.setFragmentResult
import com.example.netbond.models.UserViewModel


class UserSearchBarFragment : Fragment() {

    private lateinit var binding: FragmentUserSearchBarBinding
    private val storageService = StorageService()
    private val utils = Utils()
    private val viewModel: UserViewModel by activityViewModels()
    private var actualUsername = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setActualUsername()
        // Inflate the layout for this fragment
        binding = FragmentUserSearchBarBinding.inflate(inflater, container, false)
        CoroutineScope(Dispatchers.Main).launch {
            setSearchBarListener()
            if (binding.pendingRequests.visibility == View.VISIBLE) {
                setReceivedRequests()
            }
        }
        return binding.root
    }

    private fun setActualUsername() {
        viewModel.user.observe(viewLifecycleOwner) {
            actualUsername = it.username!!
        }
    }

    private fun setSearchBarListener() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.usersList.removeAllViews()
                if (!s.isNullOrEmpty()) {
                    binding.pendingRequests.visibility = View.GONE
                    setSearchUsers(s)
                } else {
                    binding.pendingRequests.visibility = View.VISIBLE
                    setReceivedRequests()
                }
            }
        })
    }

    private fun setSearchUsers(searchedText: CharSequence) {
        CoroutineScope(Dispatchers.Main).launch {
            val users = storageService.getUsers()
            for (user in users) {
                if (user.name!!.contains(searchedText)) {
                    val bind = SearchUserTemplateBinding.inflate(layoutInflater, binding.usersList, true)
                    bind.nameUser.text = user.name
                    bind.userPoints.text = user.n_points.toString()
                    val username = "@" + user.username
                    bind.userName.text = username
                    Glide.with(this@UserSearchBarFragment).load(user.profile_image).into(bind.userImage)
                    bind.root.setOnClickListener { view ->
                        view.setBackgroundColor(resources.getColor(R.color.gray, null))
                        setFragmentResult("requestKey", bundleOf("bundleKey" to user.username))
                        findNavController().navigate(R.id.externalUserProfileFragment)
                    }
                }
            }
        }
    }

    private fun setReceivedRequests() {
        CoroutineScope(Dispatchers.Main).launch {
            val requests = storageService.getReceivedRequests(actualUsername)
            for (request in requests) {
                val bind = AcceptUserTemplateBinding.inflate(layoutInflater, binding.usersList, true)
                bind.nameUser.text = request.name
                val username = "@" + request.username
                bind.userName.text = username
                Glide.with(this@UserSearchBarFragment).load(request.profile_image).into(bind.userImage)
                bind.userPoints.text = request.n_points.toString()
                bind.acceptButton.setOnClickListener { acceptUser(request.username!!, bind) }
            }
        }
    }

    private fun acceptUser(username: String, bind: AcceptUserTemplateBinding) {
        CoroutineScope(Dispatchers.Main).launch {
            // Actual User
            storageService.deleteReceivedRequest(actualUsername, username)
            storageService.addFollower(actualUsername, username)
            storageService.incrementFollowers(actualUsername, 1)

            // Accepted User
            storageService.deleteSentRequest(username, actualUsername)
            storageService.addFollowing(username, actualUsername)
            storageService.incrementFollowings(username, 1)

            val message = "Successfully added $username to your followers"
            utils.displayMessage(requireContext(), message)

            // Refresh Received Requests
            binding.usersList.removeView(bind.root)
        }
    }

}