package pt.ipca.scoutsbag.loginAndRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.activityManagement.ActivityDetailsActivity

class ForgotPassword : AppCompatActivity() {

    private var editTextEmailForgot: EditText? = null
    private var buttonForgotPass: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //check internet connection
        Utils.connectionLiveData(this)

        //hide topbar
        this.supportActionBar?.hide()

        editTextEmailForgot = findViewById<EditText>(R.id.editTextEmailForgot)
        val button = findViewById<Button>(R.id.buttonForgot)

        button.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val client = OkHttpClient()

                val userEmailJsonObject = JSONObject()
                userEmailJsonObject.put("email", editTextEmailForgot?.text.toString())

                //println(postBody)
                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    userEmailJsonObject.toString()
                )

                val request = Request.Builder()
                    .url("http://3.8.19.24:60000/forgot")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    val messageBodyJsonStr = response.body?.string().toString()
                    val messageJsonObject = JSONObject(messageBodyJsonStr)
                    val message = messageJsonObject.getString("message")

                    if(message == "Email does not exist!") {
                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(this@ForgotPassword, "Não existe nenhuma conta com este email!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        GlobalScope.launch(Dispatchers.Main) {
                            val intent = Intent(this@ForgotPassword, RecoverPassword::class.java)
                            val messageJsonObject = JSONObject(messageBodyJsonStr)
                            intent.putExtra("newPassDetails", messageJsonObject.toString())
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        buttonForgotPass?.setOnClickListener {

        }

    }
}
