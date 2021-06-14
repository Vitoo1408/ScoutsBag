package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.scoutsteste1.Catalog
import com.example.scoutsteste1.Instruction
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
    lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_instruction)

        checkConnectivity()

        var editTextInstructionText = findViewById<EditText>(R.id.editTextInstructionText)
        val buttonSaveInstruction = findViewById<Button>(R.id.buttonSaveInstruction)
        instructionAddImage = findViewById<ImageView>(R.id.instructionAddImage)

        var id = ""
        val bundle = intent.extras

        bundle?.let{
            id = it.getString("id").toString()
        }

        var changeActivity: ()->Unit = {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
        }

        // When the user clicks on the material image to change it
        instructionAddImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        buttonSaveInstruction.setOnClickListener {

            val fileName = Utils.getFileName(this, imageUri!!)
            val filePath = Utils.getUriFilePath(this, imageUri!!)

            GlobalScope.launch(Dispatchers.IO){

                if(imageUri != null) {
                    Utils.uploadImage(filePath!!, fileName) {
                        imageUrl = it
                    }
                }

                val instruction = Instruction(
                    null,
                    editTextInstructionText.text.toString(),
                    imageUrl,
                    id.toInt()
                )

                Backend.addInstruction(instruction,changeActivity)
            }

        }

    }

    /*
       This function happen after picking photo, and make changes in the activity
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            instructionAddImage?.setImageURI(data?.data)
            imageUri = data?.data
        }
    }
}