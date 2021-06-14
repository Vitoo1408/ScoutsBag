package pt.ipca.scoutsbag.inventoryManagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn.imageUrl
import pt.ipca.scoutsbag.models.Material

class CreateMaterialActivity : ActivityImageHelper() {

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
        checkConnectivity()

        // Variables in the activity
        val textViewName = findViewById<TextView>(R.id.editTextMaterialName)
        val textViewQuantity = findViewById<TextView>(R.id.editTextMaterialQuantity)
        val buttonCancel = findViewById<TextView>(R.id.button_cancel)
        val buttonConfirm = findViewById<TextView>(R.id.button_confirm)
        materialImage = findViewById(R.id.catalogAddImage)

        // Button Events
        buttonCancel.setOnClickListener { returnActivity }

        // When the user clicks on the material image to change it
        materialImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMaterialType)

        var materialType = "Equipamento"
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId == R.id.radioButtonEquipment)
                materialType = "Equipamento"
            if(checkedId == R.id.radioButtonConsumable)
                materialType = "Consumivel"
        }

        buttonConfirm.setOnClickListener {

            // Variables
            val fileName = Utils.getFileName(this, imageUri!!)
            val filePath = Utils.getUriFilePath(this, imageUri!!)

            GlobalScope.launch(Dispatchers.IO) {

                // Get image Url
                if(imageUri != null) {
                    Utils.uploadImage(filePath!!, fileName) {
                        imageUrl = it
                    }
                }

                // Create the material
                val material = Material(
                    null,
                    textViewName.text.toString(),
                    textViewQuantity.text.toString().toInt(),
                    if (imageUrl != null) imageUrl else "",
                    materialType
                )

                // Add the material
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