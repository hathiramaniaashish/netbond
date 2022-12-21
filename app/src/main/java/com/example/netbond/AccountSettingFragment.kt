package com.example.netbond

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
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
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        imagePickerActivityResult.launch(intent)
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

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data

                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension
                val sd = getFileName(this.requireContext(), imageUri!!)

                // Upload Task with upload to directory 'file'
                // and name of the file remains same
                val uploadTask = fStoreRef.child("profiles/$sd").putFile(imageUri)

                // On success, download the file URL and display it
                uploadTask.addOnSuccessListener {
                    // using glide library to display the image
                    fStoreRef.child("profiles/$sd").downloadUrl.addOnSuccessListener {
                        setNewImage(it.toString())

                        Log.e("Firebase", "download passed")
                    }.addOnFailureListener {
                        Log.e("Firebase", "Failed in downloading")
                    }
                }.addOnFailureListener {
                    Log.e("Firebase", "Image Upload fail")
                }
            }
        }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }


}