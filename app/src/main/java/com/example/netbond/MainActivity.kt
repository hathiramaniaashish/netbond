package com.example.netbond

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.example.netbond.databinding.ActivityMainBinding
import com.example.netbond.models.UserViewModel
import com.example.netbond.services.AuthService
import com.example.netbond.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authService = AuthService()
    private val storageService = StorageService()
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewModel()
        window.statusBarColor = getColor(R.color.background)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.setOnClickListener { hideKeyboard() }
        setUpBottomNav()
        setBtnAddBond()
        setContentView(binding.root)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        currentFocus?.clearFocus()
    }

    private fun setViewModel() {
        CoroutineScope(Dispatchers.Main).launch {
            val userEmail = authService.checkUserSignedIn()
            if (userEmail != null) {
                val user = storageService.getUserByEmail(userEmail)
                viewModel.setUser(user)
            }
        }
    }

    private fun setUpBottomNav() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.userSearchBarFragment)
                    true
                }
                R.id.home -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.feedFragment)
                    true
                }
                R.id.profile -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.userProfileFragment)
                    true
                }
                else -> {false}
            }
        }
    }

    private fun setBtnAddBond() {
        binding.fab.setOnClickListener{
            binding.fab.visibility = View.GONE
            findNavController(R.id.nav_host_fragment).navigate(R.id.bondCreationFragment)
        }
    }

}