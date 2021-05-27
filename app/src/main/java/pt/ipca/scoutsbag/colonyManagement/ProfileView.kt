package pt.ipca.scoutsbag.userManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.User

class ProfileView : AppCompatActivity()
{
    private var userProfileJsonStr: String? = null
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)

        val textName = findViewById<TextView>(R.id.scoutName)
        val textSection = findViewById<TextView>(R.id.scoutSection)
        val textTeam = findViewById<TextView>(R.id.scoutTeam)
        val textNIN = findViewById<TextView>(R.id.textNIN)
        val textPhone = findViewById<TextView>(R.id.textPhone)
        val textMail = findViewById<TextView>(R.id.textMail)
        val textBirthDate = findViewById<TextView>(R.id.textBirthDate)
        val textAddress = findViewById<TextView>(R.id.textAddress)
        val textPostalCode = findViewById<TextView>(R.id.textPostalCode)
        val butEdit = findViewById<Button>(R.id.editProfile)

        val profileTemp = User.fromJson(JSONObject(userProfileJsonStr))
/*
        butEdit.setOnClickListener() {
            val intent = Intent(activity, EditProfile::class.java)
            startActivity(intent)
        }

        GlobalScope.launch(Dispatchers.IO)
        {
            val client = OkHttpClient()

            val user = User(
                profileTemp.idUser,
                textName.text.toString(),
                textBirthDate.text.toString(),
                textMail.text.toString(),
                null,
                null,
                textPhone.text.toString(),
                null,
                textAddress.text.toString(),
                textNIN.text.toString(),
                textPostalCode.text.toString(),
                null,
                null,
                null,
            )

            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                user.toJson().toString()
            )

            val request = Request.Builder().url("http://3.8.19.24:60000/api/v1/users/${profileTemp.idUser}")
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Log.d("users", response.message)
                GlobalScope.launch(Dispatchers.Main) {
                    if (response.message == "OK") {
                        findNavController().popBackStack()
                    }
                }
            }
        }
       */
    }

}