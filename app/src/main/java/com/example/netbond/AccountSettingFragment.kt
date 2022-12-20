package com.example.netbond

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.netbond.models.UserViewModel
import com.example.netbond.services.AuthService
import com.example.netbond.services.StorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class AccountSettingFragment : Fragment(R.layout.fragment_account_settings) {

    private val db = StorageService()
    private val authService = AuthService()
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private val fStoreRef = FirebaseStorage.getInstance().reference
    private val viewModel: UserViewModel by activityViewModels()
    var userDocID:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        userDocID = viewModel.user.value?.userDocID
        setUserDocID()
        getUserData()
        setImageUploader()

        val txtLogOut = requireView().findViewById<TextView>(R.id.txt_Log_Out)
        txtLogOut.setOnClickListener {
            authService.logOut()
            val intent = Intent(this.context, LoginActivity().javaClass)
            startActivity(intent)
        }

        val btnSaveSettings = requireView().findViewById<Button>(R.id.btn_save_settings)
        btnSaveSettings.setOnClickListener { updateUserData() }
    }

    private fun setUserDocID() {
        viewModel.user.observe(viewLifecycleOwner) {
            userDocID = it.userDocID!!
        }
    }

    private fun getUserData() {

        // El botón debería tener la imagen como fondo
        // val imgProfile = findViewById<ImageView>(R.id.img_profile)
        val editName = view?.findViewById<TextView>(R.id.edit_name)
        val editUsername = view?.findViewById<TextView>(R.id.edit_username)
        val editEmail = view?.findViewById<TextView>(R.id.edit_email)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUserByDocID(userDocID!!)

            if (user != null) {
                editName?.text = user.name
                editUsername?.text = user.username
                editEmail?.text = user.email
            }
        }
    }

    private fun updateUserData() {
        val editName = view?.findViewById<TextView>(R.id.edit_name)
        val editUsername = view?.findViewById<TextView>(R.id.edit_username)
        val editEmail = view?.findViewById<TextView>(R.id.edit_email)

        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUserByDocID(userDocID!!)
            if (user != null) {
                user.name = editName?.text.toString()
                user.username = editUsername?.text.toString()
                user.email = editEmail?.text.toString()
            }
            db.updateUser(user)
        }

    }

    fun setImageUploader() {
//        btn_choose_image = view.findViewById(R.id.btn_choose_image)
        val btnChangeProfile = requireView().findViewById<Button>(R.id.btn_change_profile)
//        val imagePreview = requireView().findViewById<ImageView>(R.id.img_profile)

        btnChangeProfile.setOnClickListener {
            launchGallery()
            CoroutineScope(Dispatchers.Main).launch {
                val newImageUrl = uploadImage()
                setNewImage(newImageUrl)
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                println("SALIÓ MAAAL 1")
                return
            }

            filePath = data.data
        }
    }

    private suspend fun uploadImage():String?{
//        val progressDialog: ProgressDialog = ProgressDialog(this.context)
//        progressDialog.setTitle("Uploading")
//        progressDialog.show()
        if (filePath != null){
            val ref = fStoreRef?.child("profiles/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)
            val newImageURL = uploadTask
                ?.await()
                ?.storage
                ?.downloadUrl
                ?.await()
                .toString()
            return newImageURL

        } else {
            println("Something went wrong while uploading")
        }
        return null
    }

    private fun setNewImage(newURI:String?) {
        if (newURI != null) {
            CoroutineScope(Dispatchers.Main).launch {
                var user = db.getUserByDocID(userDocID!!)
                user!!.profile_image = newURI
                db.updateUser(user)
            }
        }
    }

}