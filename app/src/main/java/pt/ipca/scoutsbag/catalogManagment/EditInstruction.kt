package pt.ipca.scoutsbag.catalogManagment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.scoutsteste1.Instruction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.w3c.dom.Text
import pt.ipca.scoutsbag.R

class EditInstruction : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_instruction)



        var editTextEditInstruction = findViewById<EditText>(R.id.editTextEditInstruction)
        var editTextEditImageUrl = findViewById<EditText>(R.id.editTextEditImageUrl)
        val buttonSaveEditInstruction = findViewById<Button>(R.id.buttonSaveEditInstruction)
        var textView1 = findViewById<TextView>(R.id.teste1)
        var textView2 = findViewById<TextView>(R.id.teste2)
        var idC = ""
        var idI = ""
        val bundle = intent.extras

        bundle?.let{
            idC = it.getString("idCatalog").toString()
            idI = it.getString("idInstruction").toString()
        }

        textView1.text = idI
        textView2.text = idC

        buttonSaveEditInstruction.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                val client = OkHttpClient()
                val instruction = Instruction(
                    idI.toInt(),
                    editTextEditInstruction.text.toString(),
                    editTextEditImageUrl.text.toString(),
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


}