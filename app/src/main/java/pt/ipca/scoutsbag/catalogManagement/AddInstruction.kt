package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.scoutsteste1.Instruction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.R

class AddInstruction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_instruction)

        var editTextInstructionText = findViewById<EditText>(R.id.editTextInstructionText)
        var editTextImageUrl = findViewById<EditText>(R.id.editTextImageUrl)
        val buttonSaveInstruction = findViewById<Button>(R.id.buttonSaveInstruction)

        var id = ""
        val bundle = intent.extras

        bundle?.let{
            id = it.getString("id").toString()
        }

        buttonSaveInstruction.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                val client = OkHttpClient()
                val instruction = Instruction(
                    null,
                    editTextInstructionText.text.toString(),
                    editTextImageUrl.text.toString(),
                    id.toInt()

                )
                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    instruction.toJson().toString()
                )
                Log.d("scoutsbag", instruction.toJson().toString())
                val request = Request.Builder()
                    .url("http://3.8.19.24:60000/api/v1/instructions")
                    .post(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    Log.d("scoutsbag", response.message)
                }
            }
            startActivity(Intent(this, SeeInstructions::class.java))
        }
    }


}