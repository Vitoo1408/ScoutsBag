package pt.ipca.scoutsbag.colonyManagement

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.ActivityImageHelper
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.User


class EditProfileActivity : ActivityImageHelper() {

    private var editImage: ImageView? = null
    private var editName: EditText? = null
    private var editNIN: EditText? = null
    private var editPhone: EditText? = null
    private var editMail: EditText? = null
    private var editBirthDate: TextView? = null
    private var editAddress: EditText? = null
    private var editPostalCode: EditText? = null
    private var butSave: Button? = null
    private var imageUri: Uri? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //check the phones internet conectivity
        checkConnectivity()

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Editar perfil"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        editImage = findViewById(R.id.profileImage)
        editName = findViewById(R.id.editTextName)
        editNIN = findViewById(R.id.editTextNIN)
        editPhone = findViewById(R.id.EditTextPhone)
        editMail = findViewById(R.id.EditTextMail)
        editBirthDate = findViewById(R.id.EditBirthDate)
        editAddress = findViewById(R.id.EditTextAddress)
        editPostalCode = findViewById(R.id.EditTextPostalCode)
        butSave = findViewById(R.id.butSaveChangesProfile)

        //load profile image
        if(UserLoggedIn.imageUrl != ""){
            Picasso.with(this).load(UserLoggedIn.imageUrl).into(editImage)
        }

        //load all user data into text views
        editName?.setText(UserLoggedIn.userName)
        editNIN?.setText(UserLoggedIn.nin)
        editPhone?.setText(UserLoggedIn.contact)
        editMail?.setText(UserLoggedIn.email)
        editBirthDate?.text = UserLoggedIn.birthDate
        editAddress?.setText(UserLoggedIn.address)
        editPostalCode?.setText(UserLoggedIn.postalCode)

        //when user clicks on his profile image to change it
        editImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        butSave?.setOnClickListener {
            if(imageUri != null) {
                val fileName = Utils.uniqueImageNameGen()
                val filePath = Utils.getUriFilePath(this, imageUri!!)

                GlobalScope.launch(Dispatchers.Main) {
                    GlobalScope.launch(Dispatchers.IO) {
                        Utils.uploadImage(filePath!!, fileName) {
                            imageUrl = it
                        }
                        //save the user data with a new image url
                        saveUserData(imageUrl)
                    }
                }
            } else {
                //save the user data without a new image url
                saveUserData(null)
            }
            Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_LONG).show()
            finish()
        }
    }


    /*
       This function happen after picking photo, and make changes in the activity
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.activity(data?.data)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                editImage?.setImageURI(result.uri)
                imageUri = result.uri
            }
        }
    }


    private fun saveUserData(imageUrl: String?) {
        //shared preferences initialization
        val preferences = getSharedPreferences("userLogin", MODE_PRIVATE)
        val editor = preferences.edit()
        val userDetails = preferences.getString("userDetails", "")

        //convert user details json object of type string to a json object
        val userDetailsJsonObject = JSONObject(userDetails)

        //create temporary profile to update the values from the editProfile activity
        val profileTemp = User.fromJson(userDetailsJsonObject)

        profileTemp.userName = editName?.text.toString()
        profileTemp.nin = editNIN?.text.toString()
        profileTemp.contact = editPhone?.text.toString()
        profileTemp.email = editMail?.text.toString()
        profileTemp.birthDate = "1990-02-10"
        profileTemp.address = editAddress?.text.toString()
        profileTemp.postalCode = editPostalCode?.text.toString()
        if(imageUrl != null) profileTemp.imageUrl = imageUrl

        //save new user details to UserLoggedIn object
        UserLoggedIn.userName = profileTemp.userName
        UserLoggedIn.nin = profileTemp.nin
        UserLoggedIn.contact = profileTemp.contact
        UserLoggedIn.email = profileTemp.email
        UserLoggedIn.birthDate = profileTemp.birthDate
        UserLoggedIn.address = profileTemp.address
        UserLoggedIn.postalCode = profileTemp.postalCode
        UserLoggedIn.imageUrl = profileTemp.imageUrl

        //save new user details as json string to sharedPrefs
        editor.putString("userDetails", profileTemp.toJson().toString())
        editor.apply()

        Log.d("profileTemp", profileTemp.toJson().toString())

        //update user to db
        Backend.editUser(profileTemp)
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}