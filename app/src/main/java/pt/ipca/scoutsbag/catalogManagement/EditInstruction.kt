package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.scoutsteste1.Instruction
import com.example.scoutsteste1.ScoutActivity
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import pt.ipca.scoutsbag.ActivityImageHelper
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn

class EditInstruction : ActivityImageHelper() {

    lateinit var instruction: Instruction
    lateinit var nameCatalog: String
    private var imageUri: Uri? = null
    var imageUrl: String? = null
    lateinit var instructionEditImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_instruction)

        //check internet connection
        Utils.connectionLiveData(this)

        var editTextEditInstruction = findViewById<EditText>(R.id.editTextEditInstruction)
        val buttonSaveEditInstruction = findViewById<Button>(R.id.buttonSaveEditInstruction)
        instructionEditImage = findViewById(R.id.instructionEditImage)

        val bundle = intent.extras
        bundle?.let{
            val instructionJsonStr = it.getString("instruction").toString()
            val instructionJson = JSONObject(instructionJsonStr)
            instruction = Instruction.fromJson(instructionJson)

            nameCatalog = it.getString("nameCatalog").toString()
        }

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = instruction.instructionText
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        editTextEditInstruction.text.append(instruction.instructionText)

        instructionEditImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        if (instruction.imageUrl != "") {
            Picasso.with(this).load(instruction.imageUrl).into(instructionEditImage)
            imageUrl = instruction.imageUrl
        } else {
            instructionEditImage.setImageResource(R.drawable.ic_upload_image)
        }

        buttonSaveEditInstruction.setOnClickListener {

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

                val client = OkHttpClient()
                val newInstruction = Instruction(
                    instruction.idInstruction,
                    editTextEditInstruction.text.toString(),
                    if (imageUrl != null) imageUrl else "",
                    instruction.idCatalog
                )
                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    newInstruction.toJson().toString()
                )
                Log.d("scoutsbag", instruction.toJson().toString())
                val request = Request.Builder()
                    .url("http://3.8.19.24:60000/api/v1/instructions/${instruction.idInstruction}")
                    .put(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    Log.d("scoutsbag", response.message)
                }

                val intent = Intent(this@EditInstruction, SeeInstructions::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("id_catalog", instruction.idCatalog)
                intent.putExtra("name_catalog", nameCatalog)
                startActivity(intent)
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
                instructionEditImage?.setImageURI(result.uri)
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