package pt.ipca.scoutsbag.loginAndRegister

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.View.OnFocusChangeListener
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
import org.w3c.dom.Text
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.User
import java.util.*
import java.util.regex.Pattern

class Register : AppCompatActivity() {

    var editTextNome: EditText? = null
    var editTextNin: EditText? = null
    var editTextTelemovel: EditText? = null
    var buttonGeneroMasc: Button? = null
    var buttonGeneroFem: Button? = null
    var editTextCalendar : EditText? = null
    var email: EditText? = null
    var morada: EditText? = null
    var codPostal: EditText? = null
    var palavraPasse: EditText? = null
    var palavraPasseConf: EditText? = null
    var buttonSave: Button? = null
    var textViewPassMatch: TextView? = null
    var tvPassLength: TextView? = null
    var tvEmailExists: TextView? = null
    var password: String? = null
    var secondPass: String? = null
    var tvLoginFromRegister: TextView? = null

    //models
    var newUser = User()

    override fun onCreate(savedInstanceState: Bundle?) {

        //hide topbar
        this.supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //find all id's
        editTextNome = findViewById(R.id.editTextNomeRegisto)
        editTextNin = findViewById(R.id.editTextNinRegisto)
        editTextTelemovel = findViewById(R.id.editTextTelemovelRegisto)
        editTextCalendar = findViewById(R.id.editTextDtNasc)
        email = findViewById(R.id.editTextEmailRegisto)
        morada = findViewById(R.id.editTextMoradaRegisto)
        codPostal = findViewById(R.id.editTextCodPostalRegisto)
        palavraPasse = findViewById(R.id.editTextPpRegisto)
        palavraPasseConf = findViewById(R.id.editTextConfPpRegisto)
        buttonGeneroMasc = findViewById(R.id.buttonMasculino)
        var buttonMascChecked = false
        buttonGeneroFem = findViewById(R.id.buttonFeminino)
        var buttonFemChecked = false
        buttonSave = findViewById(R.id.buttonLogIn)
        textViewPassMatch = findViewById(R.id.textViewPassMatch)
        tvPassLength = findViewById(R.id.textViewCharacters)
        tvEmailExists = findViewById(R.id.textViewEmailExists)
        tvLoginFromRegister = findViewById(R.id.textViewLoginFromRegister)

        tvLoginFromRegister?.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        //Calendar
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //select date of birth
        editTextCalendar?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                    editTextCalendar?.setText(mDay.toString() + "-" + (mMonth + 1).toString() + "-" + mYear.toString())
                }, year, month, day)
                dpd.show()
            }
        }

        //send json object to db
        buttonSave?.setOnClickListener {
            if(validateFields()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val client = OkHttpClient()
                    //creation of the new object user that will be posted to db
                    newUser.userName = editTextNome?.text.toString()
                    if(editTextNin?.text.toString().isNotEmpty()) newUser.nin = editTextNin?.text.toString() else newUser.nin = null
                    newUser.contact = editTextTelemovel?.text.toString()
                    newUser.birthDate = "$year-$month-$day"
                    newUser.email = email?.text.toString()
                    newUser.address = morada?.text.toString()
                    newUser.postalCode = codPostal?.text.toString()
                    newUser.pass = palavraPasse?.text.toString()
                    newUser.imageUrl = null
                    newUser.codType = "ESC"
                    if (buttonMascChecked) newUser.gender = "m" else newUser.gender = "f"
                    newUser.accepted = 0
                    newUser.userActive = 0
                    newUser.idTeam = null

                    val postBody = newUser.toJson()
                    Log.d("postBody", postBody.toString())
                    //println(postBody)

                    val requestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        postBody.toString()
                    )

                    Log.d("requestBody", requestBody.toString())

                    val request = Request.Builder()
                        .url("http://3.8.19.24:60000/api/v1/users")
                        .post(requestBody)
                        .build()

                    client.newCall(request).execute().use { response ->
                        val messageBodyJsonStr = response.body?.string().toString()
                        val messageJsonObject = JSONTokener(messageBodyJsonStr).nextValue() as JSONObject
                        val message = messageJsonObject.getString("message")
                        Log.d("message", message)
                        if(message == "User Created Successfully") {
                            showDialogAfterRegistration()
                        } else {
                            GlobalScope.launch(Dispatchers.Main) {
                                tvEmailExists?.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }

        //select gender male
        buttonGeneroMasc?.setOnClickListener {
            if(!buttonMascChecked) {
               if(buttonFemChecked) {
                   changeColortoGray(buttonGeneroFem!!)
               }
                changeColortoOrange(buttonGeneroMasc!!)
                buttonMascChecked = true
            } else {
                changeColortoGray(buttonGeneroMasc!!)
                buttonMascChecked = false
            }
        }

        //select gender female
        buttonGeneroFem?.setOnClickListener {
            if(!buttonFemChecked) {
                if(buttonMascChecked) {
                    changeColortoGray(buttonGeneroMasc!!)
                }
                changeColortoOrange(buttonGeneroFem!!)
                buttonFemChecked = true
            } else {
                changeColortoGray(buttonGeneroFem!!)
                buttonFemChecked = false
            }
        }

        //show textView to confirm that passwords match after second password is clicked
        palavraPasseConf?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textViewPassMatch?.visibility = View.VISIBLE
            }
        }

        //update the textView to confirm that passwords match when passwords match or not
        palavraPasseConf?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                secondPass = palavraPasseConf?.text.toString()
                if(palavraPasse?.text.toString() == secondPass){
                    textViewPassMatch?.text = "As palavras passe coincidem."
                    textViewPassMatch?.setTextColor(resources.getColor(R.color.green))
                } else {
                    textViewPassMatch?.text = "As palavras passe não coincidem."
                    textViewPassMatch?.setTextColor(resources.getColor(R.color.red))
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //validate that the password meets the minimum requirement
        palavraPasse?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                secondPass = palavraPasseConf?.text.toString()
                if(palavraPasse?.text.toString() == secondPass){
                    textViewPassMatch?.text = "As palavras passe coincidem."
                    textViewPassMatch?.setTextColor(resources.getColor(R.color.green))
                } else {
                    textViewPassMatch?.text = "As palavras passe não coincidem."
                    textViewPassMatch?.setTextColor(resources.getColor(R.color.red))
                }

                password = palavraPasse?.text.toString()
                validatePassword(password!!)
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    //show a confirmation dialog after registration
    private fun showDialogAfterRegistration() {
        var dialog = DialogAfterRegister()
        dialog.show(supportFragmentManager, "customDialog")
    }

    //change gender button background resource to custom_button_white
    private fun changeColortoOrange(button: Button){
        button.setBackgroundResource(R.drawable.custom_button_white)
    }

    //change gender button background resource to custom_button_white_unfocused
    private fun changeColortoGray(button: Button){
        button.setBackgroundResource(R.drawable.custom_button_white_unfocused)
    }

    //validate every registration form field
    private fun validateFields() : Boolean {
        if(editTextNome?.text.toString().isEmpty()){
            editTextNome?.error = "O nome deve estar preenchido!"
            return false
        } else if (editTextNin?.text.toString().isEmpty()){
            editTextNin?.error = "O nin deve estar preenchido!"
            return false
        } else if (editTextTelemovel?.text.toString().isEmpty()){
            editTextTelemovel?.error = "O nº de telemóvel deve estar preenchido!"
            return false
        } else if (editTextCalendar?.text.toString().isEmpty()){
            editTextCalendar?.error = "A data de nascimento deve estar preenchida!"
            return false
        } else if (email?.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email?.text.toString()).matches()){
            email?.error = "O email está incorreto!"
            return false
        } else if (morada?.text.toString().isEmpty()){
            morada?.error = "A morada deve estar preenchido!"
            return false
        } else if (codPostal?.text.toString().isEmpty()){
            codPostal?.error = "O código postal deve estar preenchido!"
            return false
        } else if (palavraPasse?.text.toString().isEmpty() || palavraPasse?.text.toString().length < 8){
            palavraPasse?.error = "A palavra passe é inválida!"
            return false
        } else if (palavraPasseConf?.text.toString().isEmpty()){
            palavraPasseConf?.error = "A confirmação da palavra passe é inválida!"
            return false
        } else if (palavraPasseConf?.text.toString() != palavraPasse?.text.toString()){
            Toast.makeText(this, "As palavras passe não coincidem.", Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }

    private fun validatePassword(password: String) {
        if(password.length < 8){
            tvPassLength?.setTextColor(resources.getColor(R.color.red))
        } else {
            tvPassLength?.setTextColor(resources.getColor(R.color.green))
        }
    }
}























