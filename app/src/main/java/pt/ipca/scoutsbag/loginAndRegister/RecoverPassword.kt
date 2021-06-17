package pt.ipca.scoutsbag.loginAndRegister

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import pt.ipca.scoutsbag.R


class RecoverPassword : AppCompatActivity() {

    private var editTextToken: EditText? = null
    private var editTextNewPass: EditText? = null
    private var newPassDetails: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        //hide topbar
        this.supportActionBar?.hide()

        //get new pass details
        newPassDetails = intent.getStringExtra("newPassDetails")
        var newPassDetailsToJson = JSONObject(newPassDetails)
        var newPassEmail = newPassDetailsToJson.getString("email")

        editTextToken = findViewById(R.id.editTextToken)
        editTextNewPass = findViewById(R.id.editTextNewPass)

        val button3 = findViewById<Button>(R.id.button3)

        button3?.setOnClickListener {
            if (editTextNewPass?.text.toString().isEmpty() || editTextNewPass?.text.toString().length < 8){
                editTextNewPass?.error = "A palavra passe é inválida!"
            } else if(editTextToken?.text.toString().isEmpty()) {
                editTextToken?.error = "Este campo não pode estar vazio!"
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val client = OkHttpClient()

                    val newPassJsonObject = JSONObject()
                    newPassJsonObject.put("email", newPassEmail)
                    newPassJsonObject.put("token", editTextToken?.text.toString())
                    newPassJsonObject.put("password", editTextNewPass?.text.toString())

                    Log.d("details", newPassJsonObject.toString())

                    val requestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        newPassJsonObject.toString()
                    )

                    val request = Request.Builder()
                        .url("http://3.8.19.24:60000/recoverPass")
                        .post(requestBody)
                        .build()

                    client.newCall(request).execute().use { response ->
                        val messageBodyJsonStr = response.body?.string().toString()
                        val messageJsonObject = JSONObject(messageBodyJsonStr)
                        val message = messageJsonObject.getString("message")
                        Log.d("messageRecover", message)

                        if(message == "Invalid token") {
                            GlobalScope.launch(Dispatchers.Main) {
                                Toast.makeText(this@RecoverPassword, "O código de verificação não é valido", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            GlobalScope.launch(Dispatchers.Main) {
                                Toast.makeText(this@RecoverPassword, "Palavra passe editada com sucesso", Toast.LENGTH_LONG).show()
                                //go back to the login or register activity
                                val intent = Intent(this@RecoverPassword, Login::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}