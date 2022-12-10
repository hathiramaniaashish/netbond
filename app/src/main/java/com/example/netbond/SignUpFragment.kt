package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.netbond.databinding.FragmentSignUpBinding
import com.example.netbond.services.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var usersRef = db.collection("users")
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

        usersRef.get().addOnSuccessListener {
            val usersList = it
            var userExists = false
            usersList.forEach { user ->
                val actualUsername = user.get("username").toString()
                if (actualUsername == username) {
                    userExists = true
                    utils.displayMessage(binding.root, "This username already exists")
                }
            }

            if (!userExists) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        addUser(email, name, username)
                        findNavController().navigate(R.id.feedFragment)
                    } else {
                        utils.displayMessage(binding.root, "Sign up failed")
                    }
                }
            }

        }
    }

    private fun addUser(email: String, name: String, username: String) {
        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "n_followers" to 0,
            "n_followings" to 0,
            "n_points" to 0,
            "name" to name,
            "profile_image" to ""
        )

        usersRef.add(user).addOnSuccessListener {
            val userRef = usersRef.document(it.id)
            userRef.collection("bonds").add(emptyMap<String, String>())
            userRef.collection("followers").add(emptyMap<String, String>())
            userRef.collection("followings").add(emptyMap<String, String>())
            userRef.collection("receivedRequests").add(emptyMap<String, String>())
            userRef.collection("sentRequests").add(emptyMap<String, String>())
        }

    }

}