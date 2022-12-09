package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.netbond.databinding.FragmentLoginBinding
import com.example.netbond.services.Utils
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val utils = Utils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.loginMessage2.setOnClickListener { navigateToSignUp() }
        binding.logIn.setOnClickListener { logIn() }
        return binding.root
    }

    private fun navigateToSignUp() {
        findNavController().navigate(R.id.signUpFragment)
    }

    private fun logIn() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                findNavController().navigate(R.id.feedFragment)
            } else {
                utils.displayMessage(binding.root, "Authentication failed")
            }
        }
    }

}