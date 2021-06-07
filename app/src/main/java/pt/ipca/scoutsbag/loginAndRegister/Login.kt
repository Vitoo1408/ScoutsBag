package pt.ipca.scoutsbag.loginAndRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import org.json.JSONTokener
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R

class Login : AppCompatActivity() {

    var editTextEmail: EditText? = null
    var editTextPass: EditText? = null
    var buttonLogin: Button? = null
    var textViewRegister: TextView? = null
    var textViewForgotPass: TextView? = null

    //models
    var newLogin = LoginModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hide top bar
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.editTextEmailLogin)
        editTextPass = findViewById(R.id.editTextPasswordLogin)
        buttonLogin = findViewById(R.id.buttonLogIn)
        textViewRegister = findViewById(R.id.textViewRegisterFromLogin)
        textViewForgotPass = findViewById(R.id.textViewForgotPass)

        textViewForgotPass?.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        textViewRegister?.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        buttonLogin?.setOnClickListener {
            if(validateFields()) {
                    val client = OkHttpClient()
                    //creation of the new object user that will be posted to db
                    newLogin.email = editTextEmail?.text.toString()
                    newLogin.pass = editTextPass?.text.toString()

                    val postBody = newLogin.toJson()
                    //Log.d("postBody", postBody.toString())
                    val requestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        postBody.toString()
                    )
                    val request = Request.Builder()
                        .url("http://3.8.19.24:60000/api/v1/users/login")
                        .post(requestBody)
                        .build()

                GlobalScope.launch(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        //get the whole body response
                        val messageBodyJsonStr = response.body?.string().toString()
                        //convert the body response to a json object
                        val messageJsonObject = JSONTokener(messageBodyJsonStr).nextValue() as JSONObject
                        val message = messageJsonObject.getString("message")
                        when (message) {
                            "Authentication successfull" -> {
                                //save the jwt generated token
                                val token = messageJsonObject.getString("token")
                                //get user info from html response
                                val jsonArrayUser = messageJsonObject.getJSONArray("userInfo").getJSONObject(0)

                                //save the loggedIn state as TRUE and the user details as a json object on the shared preferences
                                val preferences = getSharedPreferences("userLogin", MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("loggedIn", "true")
                                editor.putString("userDetails", jsonArrayUser.toString())
                                editor.apply()


                                //start the main activity after successfull login
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(this@Login, "Inicio de sessão com sucesso", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this@Login, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                }
                            }
                            "Account not active" -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(this@Login, "Conta inativa", Toast.LENGTH_LONG).show()
                                }
                            }
                            "No email found!" -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(this@Login, "Email não registado", Toast.LENGTH_LONG).show()
                                }
                            }
                            else -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(this@Login, "Dados incorretos", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //validate every registration form field
    private fun validateFields() : Boolean {
        if(!Patterns.EMAIL_ADDRESS.matcher(editTextEmail?.text.toString()).matches()){
            editTextEmail?.error = "O email é inválido!"
            return false
        } else if (editTextPass?.text.toString().isEmpty()){
            editTextPass?.error = "A password deve estar preenchida!"
            return false
        } else{
            return true
        }
    }
}