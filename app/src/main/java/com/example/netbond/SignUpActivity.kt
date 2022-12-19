package com.example.netbond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.example.netbond.databinding.ActivitySignUpBinding
import com.example.netbond.models.User
import com.example.netbond.services.StorageService
import com.example.netbond.services.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val storageService = StorageService()
    private val auth = FirebaseAuth.getInstance()
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        binding.root.setOnClickListener { hideKeyboard() }
        binding.signUpMessage2.setOnClickListener { navigateToActivity(LoginActivity()) }
        binding.signUp.setOnClickListener { signUp() }
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
                            navigateToActivity(MainActivity())
                        } else {
                            utils.displayMessage(this@SignUpActivity, "Sign up failed")
                        }
                    }

                } else {
                    utils.displayMessage(this@SignUpActivity, "This username already exists")
                }
            }
        } else {
            utils.displayMessage(this@SignUpActivity, "All fields must be filled")
        }
    }

}