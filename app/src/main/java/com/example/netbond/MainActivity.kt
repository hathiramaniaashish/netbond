package com.example.netbond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import com.example.netbond.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userProfileFragment = UserProfileFragment()
        val accountSettingFragment = AccountSettingFragment()
        val externalUserProfileFragment = ExternalUserProfileFragment()
        val bondCreationFragment = BondCreationFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, userProfileFragment)
            commit()
        }
    }

}