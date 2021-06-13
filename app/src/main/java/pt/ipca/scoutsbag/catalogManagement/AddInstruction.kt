package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.scoutsteste1.Catalog
import com.example.scoutsteste1.Instruction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
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

        var changeActivity: ()->Unit = {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
        }

        buttonSaveInstruction.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                val instruction = Instruction(
                    null,
                    editTextInstructionText.text.toString(),
                    editTextImageUrl.text.toString(),
                    id.toInt()
                )

                Backend.addInstruction(instruction,changeActivity)
            }

        }

    }
}