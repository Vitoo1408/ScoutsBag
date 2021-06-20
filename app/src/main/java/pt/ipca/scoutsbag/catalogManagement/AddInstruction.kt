package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.scoutsteste1.Catalog
import com.example.scoutsteste1.Instruction
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.*

class AddInstruction : ActivityImageHelper() {

    private var imageUri: Uri? = null
    lateinit var instructionAddImage : ImageView
    var imageUrl: String? = null
    lateinit var editTextInstructionText: EditText
    lateinit var buttonSaveInstruction: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_instruction)

        //check internet connection
        Utils.connectionLiveData(this)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Adicionar instrução"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)


        editTextInstructionText = findViewById(R.id.editTextInstructionText)
        buttonSaveInstruction = findViewById(R.id.buttonSaveInstruction)
        instructionAddImage = findViewById(R.id.instructionAddImage)

        editTextInstructionText.addTextChangedListener(textWatcher)

        var id: Int? = null
        var nameCatalog: String? = null
        val bundle = intent.extras

        bundle?.let{
            id = it.getInt("id_catalog").toString().toInt()
            nameCatalog = it.getString("name_catalog").toString()
        }


        var changeActivity: ()->Unit = {
            val intent = Intent(this, SeeInstructions::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("id_catalog", id)
            intent.putExtra("name_catalog", nameCatalog)
            startActivity(intent)
        }

        // When the user clicks on the material image to change it
        instructionAddImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        buttonSaveInstruction.setOnClickListener {

            var fileName: String? = null
            var filePath: String? = null

            if (imageUri != null) {
                fileName = Utils.uniqueImageNameGen()
                filePath = Utils.getUriFilePath(this, imageUri!!)
            }

            GlobalScope.launch(Dispatchers.IO){

                if(imageUri != null) {
                    Utils.uploadImage(filePath!!, fileName) {
                        imageUrl = it
                    }
                }

                val instruction = Instruction(
                    null,
                    editTextInstructionText.text.toString(),
                    if (imageUrl != null) imageUrl else "",
                    id
                )

                Backend.addInstruction(instruction,changeActivity)
            }
        }
    }


    // This function check if all data is written so the user can click in the confirm button
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            // Verify if all data is written and selected
            buttonSaveInstruction.isEnabled = (
                        editTextInstructionText.text.toString().trim().isNotEmpty()
                    )

            if (buttonSaveInstruction.isEnabled) {
                // Inactivate accept button
                buttonSaveInstruction.setBackgroundResource(R.drawable.custom_button_orange)
                buttonSaveInstruction.setTextColor(resources.getColor(R.color.white))
            }
            else {
                // Inactivate accept button
                buttonSaveInstruction.setBackgroundResource(R.drawable.custom_button_white)
                buttonSaveInstruction.setTextColor(resources.getColor(R.color.orange))
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
                instructionAddImage?.setImageURI(result.uri)
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