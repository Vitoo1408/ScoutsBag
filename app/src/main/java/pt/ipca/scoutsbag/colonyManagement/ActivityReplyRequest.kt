package pt.ipca.scoutsbag.colonyManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User

class ActivityReplyRequest : AppCompatActivity() {

    // Global Variables
    lateinit var user: User
    var id = ""
    var teams : List<Team> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply_request)

        // Get the values to the list
        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllTeams {
                teams = it
            }
        }

        // Get the user from the last activity
        val userJsonStr = intent.getStringExtra("user")!!
        user = User.fromJson(JSONObject(userJsonStr))

        // Variables
        val textName = findViewById<TextView>(R.id.scoutName)
        val textNIN = findViewById<TextView>(R.id.textNIN)
        val textPhone = findViewById<TextView>(R.id.textPhone)
        val textMail = findViewById<TextView>(R.id.textMail)
        val textBirthDate = findViewById<TextView>(R.id.textBirthDate)
        val textAddress = findViewById<TextView>(R.id.textAddress)
        val textPostalCode = findViewById<TextView>(R.id.textPostalCode)

        val buttonAcceptUser = findViewById<Button>(R.id.buttonAcceptUser)
        val buttonRefuseUser = findViewById<Button>(R.id.buttonRefuseUser)


        // Set data
        textName.text = user.userName
        textNIN.text = user.nin
        textPhone.text = user.contact
        textMail.text = user.email
        textBirthDate.text = user.birthDate
        textAddress.text = user.address
        textPostalCode.text = user.postalCode
    }
}