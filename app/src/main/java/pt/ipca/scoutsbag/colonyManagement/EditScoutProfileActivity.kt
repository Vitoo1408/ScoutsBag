package pt.ipca.scoutsbag.colonyManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.json.JSONObject
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.User

class EditScoutProfileActivity : AppCompatActivity() {

    lateinit var user: User
    private var profileImage: ImageView? = null
    private var editNin: EditText? = null
    private var checkBoxDirigente: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_scout_profile)

        //check internet connection
        Utils.connectionLiveData(this)

        val userJsonStr = intent.getStringExtra("user")!!
        user = User.fromJson(JSONObject(userJsonStr))

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = user.userName
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        profileImage = findViewById(R.id.profileImage)
        editNin = findViewById(R.id.editTextNIN)
        checkBoxDirigente = findViewById(R.id.checkBoxDirigente)

        if(user.codType == "adm") {
            checkBoxDirigente?.visibility = View.VISIBLE
        }

        //load profile image
        if(user.imageUrl != "" && user.imageUrl != "null") {
            Picasso.with(this).load(user.imageUrl).into(profileImage)
        } else {
            profileImage?.setImageResource(R.drawable.ic_perfil)
        }

        if(UserLoggedIn.nin != "null") {
            editNin?.setText(UserLoggedIn.nin)
        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}