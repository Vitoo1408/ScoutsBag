package pt.ipca.scoutsbag.colonyManagement

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import java.util.jar.Manifest
import pt.ipca.scoutsbag.models.User as User


class EditProfileActivity : AppCompatActivity() {

    companion object {
        private const val IMAGE_REQUEST_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

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
                val fileName = Utils.getFileName(this, imageUri!!)
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

    //Open phone's gallery to pick photo
    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    //after picking photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            editImage?.setImageURI(data?.data)
            imageUri = data?.data
        }
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@EditProfileActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@EditProfileActivity, arrayOf(permission), requestCode)
        } else {
            pickImageGallery()
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageGallery()
            } else {
                Toast.makeText(this@EditProfileActivity, "Acesso ao armazenamento interno negado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkConnectivity() {
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo

        if (null == activeNetwork) {
            val dialogBuilder = AlertDialog.Builder(this)
            // set message of alert dialog
            dialogBuilder.setMessage("Tenha a certeza que o WI-FI ou os dados móveis estão ligados.")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Tentar novamente", DialogInterface.OnClickListener { dialog, id ->
                    recreate()
                })
                // negative button text and action
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Sem conexão à internet")
            alert.setIcon(R.mipmap.ic_launcher)
            // show alert dialog
            alert.show()
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