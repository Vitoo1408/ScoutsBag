package pt.ipca.scoutsbag.userManagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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
import pt.ipca.scoutsbag.models.User as User


class EditProfile : AppCompatActivity()
{
    private var userProfileJsonStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)

        // fazer intent get
        val profileTemp = User.fromJson(JSONObject(userProfileJsonStr))

        val editName = findViewById<EditText>(R.id.editTextName)
        val editNIN = findViewById<EditText>(R.id.editTextNIN)
        val editPhone = findViewById<EditText>(R.id.EditTextPhone)
        val editMail = findViewById<EditText>(R.id.EditTextMail)
        val editBirthDate = findViewById<TextView>(R.id.EditBirthDate)
        val editAddress = findViewById<EditText>(R.id.EditTextAddress)
        val editPostalCode = findViewById<EditText>(R.id.EditTextPostalCode)
        val editImage = findViewById<ImageButton>(R.id.butChangeImage)
        val butSave = findViewById<Button>(R.id.butSaveChangesProfile)


        editName.hint = profileTemp.userName
        editNIN.hint = profileTemp.nin
        editPhone.hint = profileTemp.contact
        editMail.hint = profileTemp.email
        editBirthDate.hint = profileTemp.bithDate
        editAddress.hint = profileTemp.adress
        editPostalCode.hint = profileTemp.postalCode

        butSave.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {

                val user = User(
                    profileTemp.idUser,
                    editName.text.toString(),
                    null,
                    editMail.text.toString(),
                    null,
                    null,
                    null,
                    null,
                    editAddress.text.toString(),
                    editNIN.text.toString(),
                    editPostalCode.text.toString(),
                    null,
                    null,
                    null)

                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    user.toJson().toString()
                )

                val request = Request.Builder().url("http://3.8.19.24:60000/api/v1/users/${profileTemp.idUser}")
                    .put(requestBody)
                    .build()

                OkHttpClient().newCall(request).execute().use { response ->

                    /*GlobalScope.launch(Dispatchers.Main) {
                        if (response.message == "OK") {
                            findNavController().popBackStack()
                        }
                    }*/
                }

            }
        }
    }
}