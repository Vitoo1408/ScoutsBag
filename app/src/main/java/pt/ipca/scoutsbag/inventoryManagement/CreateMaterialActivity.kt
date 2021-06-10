package pt.ipca.scoutsbag.inventoryManagement

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.colonyManagement.ActivityUserRequest
import pt.ipca.scoutsbag.colonyManagement.EditProfileActivity
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn.imageUrl
import pt.ipca.scoutsbag.models.Material
import pt.ipca.scoutsbag.models.User

class CreateMaterialActivity : ActivityWithImageTools() {

    private var imageUri: Uri? = null
    var materialImage: ImageView? = null

    // This function is for return to the previous activity after a operation
    var returnActivity: ()->Unit = {
        val returnIntent = Intent(this, InventoryActivity::class.java)
        startActivity(returnIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_material)

        // Variables in the activity
        val textViewName = findViewById<TextView>(R.id.editTextMaterialName)
        val textViewQuantity = findViewById<TextView>(R.id.editTextMaterialQuantity)
        val buttonCancel = findViewById<TextView>(R.id.button_cancel)
        val buttonConfirm = findViewById<TextView>(R.id.button_confirm)
        materialImage = findViewById(R.id.materialAddImage)

        // Button Events
        buttonCancel.setOnClickListener { returnActivity }

        // When the user clicks on the material image to change it
        materialImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        buttonConfirm.setOnClickListener {

            // Get image Url
            if(imageUri != null) {

                val fileName = Utils.getFileName(this, imageUri!!)
                val filePath = Utils.getUriFilePath(this, imageUri!!)

                GlobalScope.launch(Dispatchers.IO) {
                    Utils.uploadImage(filePath!!, fileName) {
                        imageUrl = it
                    }
                }
            }

            // Create the material
            val material = Material(
                null,
                textViewName.text.toString(),
                textViewQuantity.text.toString().toInt(),
                if (imageUrl != null) imageUrl else null,
                "Consumivel"
            )

            // Add the material
            GlobalScope.launch(Dispatchers.IO) {
                Backend.addMaterial(material, returnActivity)
            }
        }

    }


    /*
       This function happen after picking photo, and make changes in the activity
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            materialImage?.setImageURI(data?.data)
            imageUri = data?.data
        }
    }

}