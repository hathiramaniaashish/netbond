package com.example.netbond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import com.bumptech.glide.Glide
import com.example.netbond.databinding.AnswerTemplateBinding
import com.example.netbond.databinding.BondTemplateBinding
import com.example.netbond.databinding.FragmentFeedBinding
import com.example.netbond.services.StorageService
import com.example.netbond.services.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val storageService = StorageService()
    private val utils = Utils()
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
        CoroutineScope(Dispatchers.Main).launch {
            var hasBonds = false
            val followings = storageService.getFollowings(actualUsername)
            for (following in followings) {
                val bonds = storageService.getUserBondsID(following.username!!)
                for (bondID in bonds) {
                    if (!storageService.hasInteracted(actualUsername, bondID)) {
                        hasBonds = true
                        val bond = storageService.getBondByID(bondID)
                        val bindBond = BondTemplateBinding.inflate(layoutInflater, binding.bonds, true)
                        Glide.with(this@FeedFragment).load(following.profile_image).into(bindBond.userImage)
                        val username = "@" + following.username
                        bindBond.username.text = username
                        bindBond.question.text = bond.question
                        val keyRight = bond.keyRight!!
                        val answers = bond.ansList!!
                        for (ans in answers) {
                            val bindButton = AnswerTemplateBinding.inflate(layoutInflater, bindBond.answers, true)
                            bindButton.answer.text = ans.value
                            bindButton.answer.setOnClickListener {
                                checkAnswer(ans.key, keyRight, it, bondID, bindBond)
                            }
                        }
                    }
                }
            }
            if (hasBonds) {
                binding.message.visibility = View.GONE
            } else {
                binding.message.visibility = View.VISIBLE
            }
        }
    }

    private fun checkAnswer(keyQuestion: String, keyRight: String, view: View, bondID: String, bind: BondTemplateBinding) {
        CoroutineScope(Dispatchers.Main).launch {
            if (keyQuestion == keyRight) {
                view.setBackgroundResource(R.drawable.right_answer_color)
                utils.displayMessage(requireContext(), "Correct! You earned +1 point")
                storageService.incrementPoints(actualUsername, 1)
            } else {
                view.setBackgroundResource(R.drawable.wrong_answer_color)
                utils.displayMessage(requireContext(), "Wrong! You lost -1 point")
                storageService.incrementPoints(actualUsername, -1)
            }
            storageService.addInteraction(bondID, actualUsername)
            delay(3000)

            // Refresh Feed
            binding.bonds.removeView(bind.root)
        }
    }

}