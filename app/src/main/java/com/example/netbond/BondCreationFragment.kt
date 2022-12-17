package com.example.netbond

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.netbond.controllers.BondController
import com.example.netbond.models.UserViewModel

class BondCreationFragment : Fragment(R.layout.fragment_bond_creation) {

    private val bondController = BondController()
    private val viewModel: UserViewModel by activityViewModels()
    var userDocID:String? = null
    private var ansList = hashMapOf<String, String>()
    private var ansId:Int = 0
    private var rightView:View? = null
    private var rightId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDocID = viewModel.userDocID!!
        setFields()
    }

    fun setFields() {

        val btnAddWrong = requireView().findViewById<Button>(R.id.btn_add_wrong_ans)
        val lvWrongAns = requireView().findViewById<ListView>(R.id.lv_wrong_ans)
        val btnCreateBond = requireView().findViewById<Button>(R.id.btn_create_bond)

        val editQuestion = view?.findViewById<TextView>(R.id.edit_question)
        val editWrongAns = view?.findViewById<TextView>(R.id.edit_wrong_ans)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        btnAddWrong?.setOnClickListener{
//            ansList.put(idQuest.plus(1),
//                Pair(false, editWrongAnsList?.text.toString()))
//            editRightAns?.text = ansList.last()

            if (editWrongAns != null && !editWrongAns.text.isNullOrEmpty()) {
                ansList.put(ansId.toString(), editWrongAns.text.toString())
                ansId = ansId + 1
                editWrongAns.text = ""
                updateAns()
            }
        }

        lvWrongAns.setOnItemClickListener() {
            parent, view, position, id ->
            rightView?.setBackgroundColor(Color.TRANSPARENT)
            if (!view.equals(rightView)) {
                view.setBackgroundColor(Color.GREEN)
                rightView = view
                rightId = id.toInt()
            }
        }


        btnCreateBond.setOnClickListener {
                bondController.shareBond(
                    userDocID!!,
                    editQuestion!!.text.toString(),
                    ansList,
                    rightId.toString()
                )
        }

    }

    private fun updateAns() {
        val lvWrongAns = requireView().findViewById<ListView>(R.id.lv_wrong_ans)
        lvWrongAns.adapter = ArrayAdapter<String>(
            this.requireContext(),
            android.R.layout.simple_list_item_1,
//            arrayOf(ansList.values.toList())
            ansList.values.toList()
        )
    }

}