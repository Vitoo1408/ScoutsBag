package pt.ipca.scoutsbag.colonyManagement

import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.DialogAfterRegister
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.User


class EditProfileActivity : ActivityImageHelper() {

    private var editImage: ImageView? = null
    private var editName: EditText? = null
    private var editNIN: EditText? = null
    private var editPhone: EditText? = null
    private var editMail: EditText? = null
    private var editAddress: EditText? = null
    private var editPostalCode: EditText? = null
    private var butSave: Button? = null
    private var imageUri: Uri? = null
    private var imageUrl: String? = null
    private var genRadioGroup: RadioGroup? = null
    private var editGender: String? = null
    lateinit var connectionLiveData: ConnectionLiveData
    lateinit var editBirthDate: TextView
    private var textViewValidatePass: TextView? = null
    private var editTextNewPass: EditText? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Editar perfil"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        //check internet connection
        Utils.connectionLiveData(this)

        //find all views id's
        editImage = findViewById(R.id.profileImage)
        editName = findViewById(R.id.editTextName)
        editNIN = findViewById(R.id.editTextNIN)
        editPhone = findViewById(R.id.EditTextPhone)
        editMail = findViewById(R.id.EditTextMail)
        editBirthDate = findViewById(R.id.EditBirthDate)
        editAddress = findViewById(R.id.EditTextAddress)
        editPostalCode = findViewById(R.id.EditTextPostalCode)
        butSave = findViewById(R.id.butSaveChangesProfile)
        genRadioGroup = findViewById(R.id.radioGroup)
        textViewValidatePass = findViewById(R.id.textViewValidatePass)
        editTextNewPass = findViewById(R.id.editProfileNewPass)

        //load profile image
        if(UserLoggedIn.imageUrl != "" && UserLoggedIn.imageUrl != "null") {
            Picasso.with(this).load(UserLoggedIn.imageUrl).into(editImage)
        } else {
            editImage?.setImageResource(R.drawable.ic_upload_image)
        }

        // edit user gender
        genRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.genderFem -> editGender = "F"
                R.id.genderMasc -> editGender = "M"
                R.id.genderOther -> editGender = "O"
            }
        }

        //load all user data into text views
        when (UserLoggedIn.gender) {
            "F" -> findViewById<RadioButton>(R.id.genderFem).isChecked = true
            "f" -> findViewById<RadioButton>(R.id.genderFem).isChecked = true
            "M" -> findViewById<RadioButton>(R.id.genderMasc).isChecked = true
            "m" -> findViewById<RadioButton>(R.id.genderMasc).isChecked = true
            "O" -> findViewById<RadioButton>(R.id.genderOther).isChecked = true
            "o" -> findViewById<RadioButton>(R.id.genderOther).isChecked = true
        }

        // Create the pop up window to select the date
        val birthDatePickerDialog = Utils.initOnlyDatePicker(editBirthDate, this)

        //set new birth date
        editBirthDate.setOnClickListener {
            birthDatePickerDialog.show()
        }

        //set new password
        editTextNewPass?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                password = editTextNewPass?.text.toString()
                validatePassword(password!!)
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        editGender = UserLoggedIn.gender
        editName?.setText(UserLoggedIn.userName)
        if(UserLoggedIn.nin != "null") editNIN?.setText(UserLoggedIn.nin)
        editPhone?.setText(UserLoggedIn.contact)
        editMail?.setText(UserLoggedIn.email)
        editBirthDate?.setText(Utils.mySqlDateToDate(Utils.mySqlDateToString(UserLoggedIn.birthDate.toString())))
        editAddress?.setText(UserLoggedIn.address)
        editPostalCode?.setText(UserLoggedIn.postalCode)

        //when user clicks on his profile image to change it
        editImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        //save user details
        butSave?.setOnClickListener {
            if (editTextNewPass?.text.toString() != "" && editTextNewPass?.text.toString().length < 8) {
                editTextNewPass?.error = "A palavra passe ?? inv??lida!"
            } else {
                if (imageUri != null) {
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
                    GlobalScope.launch(Dispatchers.IO) {
                        //save the user data without a new image url
                        saveUserData(null)
                    }
                }
                Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_LONG).show()
                finish()
            }
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
        profileTemp.birthDate = Utils.dateToMySql(editBirthDate.text.toString())
        profileTemp.address = editAddress?.text.toString()
        profileTemp.postalCode = editPostalCode?.text.toString()
        profileTemp.gender = editGender
        if(imageUrl != null) profileTemp.imageUrl = imageUrl
        if(editTextNewPass?.text.toString() != "") {
            profileTemp.pass = editTextNewPass?.text.toString()
        }

        //save new user details to UserLoggedIn object
        UserLoggedIn.userName = profileTemp.userName
        UserLoggedIn.nin = profileTemp.nin
        UserLoggedIn.contact = profileTemp.contact
        UserLoggedIn.email = profileTemp.email
        UserLoggedIn.birthDate = profileTemp.birthDate
        UserLoggedIn.address = profileTemp.address
        UserLoggedIn.postalCode = profileTemp.postalCode
        UserLoggedIn.gender = profileTemp.gender
        UserLoggedIn.imageUrl = profileTemp.imageUrl

        //save new user details as json string to sharedPrefs
        editor.putString("userDetails", profileTemp.toJson().toString())
        editor.apply()

        //update user to db
        Backend.editUser(profileTemp) {

        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun validatePassword(password: String) {
        if(password.length < 8){
            textViewValidatePass?.setTextColor(resources.getColor(R.color.red))
        } else {
            textViewValidatePass?.setTextColor(resources.getColor(R.color.green))
        }
    }
}