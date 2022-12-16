package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.netbond.databinding.FragmentSignUpBinding
import com.example.netbond.models.User
import com.example.netbond.services.StorageService
import com.example.netbond.services.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val storageService = StorageService()
    private val auth = FirebaseAuth.getInstance()
    private val utils = Utils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.signUpMessage2.setOnClickListener { navigateToLogin() }
        binding.signUp.setOnClickListener { signUp() }
        return binding.root
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.loginFragment)
    }

    private fun signUp() {
        val name = binding.name.text.toString()
        val email = binding.email.text.toString()
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                if (!storageService.existsUser(username)) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = User(
                                "https://firebasestorage.googleapis.com/v0/b/netbond-app.appspot.com/o/profiles%2Fdefault_avatar_img.jfif?alt=media&token=20fa02a9-bd7d-48af-9641-b35c9f86b735",
                                name, username, 0, 0, 0, email)
                            storageService.addNewUser(user)
                            findNavController().navigate(R.id.feedFragment)
                        } else {
                            utils.displayMessage(requireContext(), "Sign up failed")
                        }
                    }

                } else {
                    utils.displayMessage(requireContext(), "This username already exists")
                }
            }
        } else {
            utils.displayMessage(requireContext(), "All fields must be filled")
        }
    }

}