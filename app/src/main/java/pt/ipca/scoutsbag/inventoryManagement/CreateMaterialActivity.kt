package pt.ipca.scoutsbag.inventoryManagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn.imageUrl
import pt.ipca.scoutsbag.models.Material

class CreateMaterialActivity : ActivityImageHelper() {

    private var imageUri: Uri? = null
    var materialImage: ImageView? = null
    lateinit var textViewName: EditText
    lateinit var textViewQuantity: EditText
    lateinit var buttonConfirm: Button

    // This function is for return to the previous activity after a operation

    var returnActivity: ()->Unit = {
        val returnIntent = Intent(this, InventoryActivity::class.java)
        returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(returnIntent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_material)

        //check internet connection
        Utils.connectionLiveData(this)

        // Variables in the activity
        textViewName = findViewById(R.id.editTextMaterialName)
        textViewQuantity = findViewById(R.id.editTextMaterialQuantity)
        val buttonCancel = findViewById<TextView>(R.id.button_cancel)
        buttonConfirm = findViewById(R.id.button_confirm)
        materialImage = findViewById(R.id.catalogAddImage)

        textViewName.addTextChangedListener(textWatcher)
        textViewQuantity.addTextChangedListener(textWatcher)

        // When the user clicks on the material image to change it
        materialImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Adicionar Material"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMaterialType)

        var materialType = "Equipamento"
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId == R.id.radioButtonEquipment)
                materialType = "Equipamento"
            if(checkedId == R.id.radioButtonConsumable)
                materialType = "Consumivel"
        }

        // Button Events
        buttonCancel.setOnClickListener {
            returnActivity
        }

        buttonConfirm.setOnClickListener {

            // Variables
            val fileName = Utils.uniqueImageNameGen()
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


    // This function check if all data is written so the user can click in the confirm button
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            // Verify if all data is written and selected
            buttonConfirm.isEnabled = (
                    textViewName.text.toString().trim().isNotEmpty() &&
                            textViewQuantity.text.toString().trim().isNotEmpty()

                    )

            if (buttonConfirm.isEnabled) {
                // Inactivate accept button
                buttonConfirm.setBackgroundResource(R.drawable.custom_button_orange)
                buttonConfirm.setTextColor(resources.getColor(R.color.white))
            }
            else {
                // Inactivate accept button
                buttonConfirm.setBackgroundResource(R.drawable.custom_button_white)
                buttonConfirm.setTextColor(resources.getColor(R.color.orange))
            }
        }

        override fun afterTextChanged(s: Editable) {}
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
                materialImage?.setImageURI(result.uri)
                imageUri = result.uri
            }
        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}