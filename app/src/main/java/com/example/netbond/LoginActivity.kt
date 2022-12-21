package com.example.netbond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.example.netbond.databinding.ActivityLoginBinding
import com.example.netbond.services.Utils
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.background)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.root.setOnClickListener { hideKeyboard() }
        binding.loginMessage2.setOnClickListener { navigateToActivity(SignUpActivity()) }
        binding.logIn.setOnClickListener { logIn() }
        setContentView(binding.root)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        currentFocus?.clearFocus()
    }

    private fun navigateToActivity(activity: AppCompatActivity) {
        val intent = Intent(this, activity.javaClass)
        startActivity(intent)
    }

    private fun logIn() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    navigateToActivity(MainActivity())
                } else {
                    utils.displayMessage(this, "Authentication failed")
                }
            }
        } else {
            utils.displayMessage(this, "All fields must be filled")
        }
    }

}