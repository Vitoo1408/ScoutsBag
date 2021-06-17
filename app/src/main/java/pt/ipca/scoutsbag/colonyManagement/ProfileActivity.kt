package pt.ipca.scoutsbag.colonyManagement

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.User

class ProfileActivity : AppCompatActivity() {

    lateinit var user: User
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Get the user from the last activity
        val userJsonStr = intent.getStringExtra("user")!!
        user = User.fromJson(JSONObject(userJsonStr))
        val userTeam = intent.getStringExtra("team")
        val userSection = intent.getStringExtra("section")

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = user.userName
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        // Variables
        val textName = findViewById<TextView>(R.id.scoutName)
        val textSection = findViewById<TextView>(R.id.scoutSection)
        val textTeam = findViewById<TextView>(R.id.scoutTeam)
        val textPhone = findViewById<TextView>(R.id.textPhone)
        val textMail = findViewById<TextView>(R.id.textMail)
        val textBirthDate = findViewById<TextView>(R.id.textBirthDate)
        val textAddress = findViewById<TextView>(R.id.textAddress)
        val textPostalCode = findViewById<TextView>(R.id.textPostalCode)
        val butEdit = findViewById<FloatingActionButton>(R.id.editProfile)
        val profileImage = findViewById<ImageView>(R.id.imageProfile)

        // Set data
        //load profile image
        if(user.imageUrl != "" && user.imageUrl != "null") {
            Picasso.with(this).load(user.imageUrl).into(profileImage)
        }
        textName.text = user.userName
        if(userSection != "") {
            textSection.text = userSection
        }
        if(userTeam != "") {
            textTeam.text = userTeam
        }

        textPhone.text = user.contact
        textMail.text = user.email
        textBirthDate.text = user.birthDate
        textAddress.text = user.address
        textPostalCode.text = user.postalCode


        butEdit.setOnClickListener() {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}