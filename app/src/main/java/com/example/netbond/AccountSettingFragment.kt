package com.example.netbond

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.netbond.services.StorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AccountSettingFragment : Fragment(R.layout.activity_account_settings) {

    private val db = StorageService()
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private val fStore = FirebaseStorage.getInstance()
    private val fStoreRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_account_setting, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        getUserData()
        setImageUploader()

        val btnSaveSettings = requireView().findViewById<Button>(R.id.btn_save_settings)
        btnSaveSettings.setOnClickListener { updateUserData() }
    }

    private fun getUserData() {

        val thisUsername = "johndoe"// getThisUsername()

        // El botón debería tener la imagen como fondo
        // val imgProfile = findViewById<ImageView>(R.id.img_profile)
        val editName = view?.findViewById<TextView>(R.id.edit_name)
        val editUsername = view?.findViewById<TextView>(R.id.edit_username)
        val editEmail = view?.findViewById<TextView>(R.id.edit_email)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUser(thisUsername)

            if (user != null) {
                editName?.text = user.name
                editUsername?.text = user.username
                editEmail?.text = user.email
            }
        }
    }

    private fun updateUserData() {

        val thisUsername = "johndoe"// getThisUsername()

        // El botón debería tener la imagen como fondo
        // val imgProfile = findViewById<ImageView>(R.id.img_profile)
        val editName = view?.findViewById<TextView>(R.id.edit_name)
        val editUsername = view?.findViewById<TextView>(R.id.edit_username)
        val editEmail = view?.findViewById<TextView>(R.id.edit_email)

        // imgProfile.setImageURI(user.profile_image)
        // Glide.with(this).load(user.profile_image).into(imgProfile)
        // Picasso.get().load(user.profile_image).into(imgProfile)

        val btnSaveSettings = requireView().findViewById<Button>(R.id.btn_save_settings)
        CoroutineScope(Dispatchers.Main).launch{
            var user = db.getUser(thisUsername)
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
            uploadImage()
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

    private fun uploadImage(){
        if (filePath != null){
            val ref = fStoreRef?.child("profiles/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

        } else {
            println("SALIÓ MAAAL 2")
        }
    }

}