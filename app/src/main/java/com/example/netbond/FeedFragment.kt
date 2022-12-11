package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.netbond.databinding.AnswerTemplateBinding
import com.example.netbond.databinding.BondTemplateBinding
import com.example.netbond.databinding.FragmentFeedBinding
import com.google.firebase.firestore.FirebaseFirestore


class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var usersRef = db.collection("users")
    private var bondsRef = db.collection("bonds")
    private var actualUsername = "johndoe"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        setFeed()
        return binding.root
    }

    private fun setFeed() {
        usersRef.whereEqualTo("username", actualUsername).get().addOnSuccessListener {
            val actualUserDoc = it.single()
            val actualUserRef = usersRef.document(actualUserDoc.id)
            actualUserRef
                .collection("followings")
                .get().addOnSuccessListener { followings ->
                    followings.forEach { user ->
                        usersRef.whereEqualTo("username", user.id).get().addOnSuccessListener {
                            val userDoc = it.single()
                            val userRef = usersRef.document(userDoc.id)
                            userRef.collection("bonds")
                                .get().addOnSuccessListener { bonds ->
                                    bonds.forEach { bond ->
                                        val bondRef = bondsRef.document(bond.id)
                                        // Check if actual user has interacted with this bond
                                        // ...
                                        bondRef.get().addOnSuccessListener { bondDoc ->
                                            val bind = BondTemplateBinding.inflate(layoutInflater, binding.bonds, true)
                                            val profileURL = userDoc.get("profile_image").toString()
                                            Glide.with(this).load(profileURL).into(bind.userImage)
                                            val username = "@" + bondDoc.get("author").toString()
                                            bind.username.text = username
                                            bind.question.text = bondDoc.get("question").toString()
                                            val answers = bondDoc.get("ansList") as HashMap<*, *>
                                            for (ans in answers) {
                                                val bindButton = AnswerTemplateBinding.inflate(layoutInflater, bind.answers, true)
                                                bindButton.answer.text = ans.value.toString()
                                            }
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }

}