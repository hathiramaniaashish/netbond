package com.example.netbond

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.netbond.databinding.FragmentUserSearchBarBinding
import com.example.netbond.databinding.SearchUserTemplateBinding
import com.google.firebase.firestore.FirebaseFirestore


class UserSearchBarFragment : Fragment() {

    private lateinit var binding: FragmentUserSearchBarBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var usersRef = db.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserSearchBarBinding.inflate(inflater, container, false)
        setSearchBarListener()
        return binding.root
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
                    usersRef.get().addOnSuccessListener {
                        val usersList = it
                        usersList.forEach { user ->
                            val name = user.get("name").toString()
                            if (name.contains(s)) {
                                val bind = SearchUserTemplateBinding.inflate(layoutInflater, binding.usersList, true)
                                bind.nameUser.text = name
                                bind.userPoints.text = user.get("n_points").toString()
                                val username = "@" + user.get("username").toString()
                                bind.userName.text = username
                                bind.user.tag = username
                            }
                        }
                    }
                }
            }

        })

    }

}