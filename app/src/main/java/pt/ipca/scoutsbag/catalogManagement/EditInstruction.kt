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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.ActivityImageHelper
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn

class EditInstruction : ActivityImageHelper() {

    private var imageUri: Uri? = null
    lateinit var instructionEditImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_instruction)

        checkConnectivity()

        var editTextEditInstruction = findViewById<EditText>(R.id.editTextEditInstruction)
        val buttonSaveEditInstruction = findViewById<Button>(R.id.buttonSaveEditInstruction)
        instructionEditImage = findViewById<ImageView>(R.id.instructionEditImage)
        var idC = ""
        var idI = ""
        val bundle = intent.extras

        bundle?.let{
            idC = it.getString("idCatalog").toString()
            idI = it.getString("idInstruction").toString()
        }

        instructionEditImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        buttonSaveEditInstruction.setOnClickListener {

            val fileName = Utils.getFileName(this, imageUri!!)
            val filePath = Utils.getUriFilePath(this, imageUri!!)

            GlobalScope.launch(Dispatchers.IO){

                if(imageUri != null) {
                    Utils.uploadImage(filePath!!, fileName) {
                        UserLoggedIn.imageUrl = it
                    }
                }

                val client = OkHttpClient()
                val instruction = Instruction(
                    idI.toInt(),
                    editTextEditInstruction.text.toString(),
                    if (UserLoggedIn.imageUrl != null) UserLoggedIn.imageUrl else "",
                    idC.toInt()


                )
                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    instruction.toJson().toString()
                )
                Log.d("scoutsbag", instruction.toJson().toString())
                val request = Request.Builder()
                    .url("http://3.8.19.24:60000/api/v1/instructions/${idI.toInt()}")
                    .put(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    Log.d("scoutsbag", response.message)
                }
            }

            startActivity(Intent(this, SeeInstructions::class.java))
        }
    }

    /*
       This function happen after picking photo, and make changes in the activity
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            instructionEditImage?.setImageURI(data?.data)
            imageUri = data?.data
        }
    }
}