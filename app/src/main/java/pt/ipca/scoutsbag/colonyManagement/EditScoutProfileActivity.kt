package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.User

class EditScoutProfileActivity : AppCompatActivity() {

    lateinit var user: User
    private var profileImage: ImageView? = null
    private var editNin: EditText? = null
    private var checkBoxDirigente: CheckBox? = null
    private var buttonSave: Button? = null
    private var teamId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_scout_profile)

        //check internet connection
        Utils.connectionLiveData(this)

        val userJsonStr = intent.getStringExtra("user")!!
        user = User.fromJson(JSONObject(userJsonStr))

        //set default teamID
        teamId = user.idTeam

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = user.userName
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        buttonSave = findViewById(R.id.butSaveChangesProfile)
        profileImage = findViewById(R.id.profileImage)
        editNin = findViewById(R.id.editTextNIN)
        checkBoxDirigente = findViewById(R.id.checkBoxDirigente)

        //only show "Dirigente" check box when logged in user is admin
        if(user.codType == "Adm") {
            checkBoxDirigente?.visibility = View.VISIBLE
        }

        //load profile image
        if(user.imageUrl != "" && user.imageUrl != "null") {
            Picasso.with(this).load(user.imageUrl).into(profileImage)
        } else {
            profileImage?.setImageResource(R.drawable.ic_perfil)
        }

        //pre fill nin edit text if not null
        if(UserLoggedIn.nin != "null") {
            editNin?.setText(user.nin)
        }


        //TODO : pre selecionar secção e equipa do escuteiro / Mudar variavel id team ao selecionar equipa diferente


        buttonSave?.setOnClickListener {

            //check if user is a "Dirigente"
            if(checkBoxDirigente?.isChecked == true) {
                user.codType == "Dir"
            } else {
                user.codType = "Esc"
            }

            //set user nin from edit text nin
            user.nin = editNin?.text.toString()

            //set user id team
            if(user.idTeam != teamId)
            user.idTeam = teamId

            //set birthdate in correct format
            user.birthDate = Utils.mySqlDateToString(user.birthDate!!)


            GlobalScope.launch(Dispatchers.IO) {
                Backend.editUser(user) {

                }
            }

            Toast.makeText(this, "Escuteiro atualizado com sucesso", Toast.LENGTH_LONG).show()

            val returnIntent = Intent(this, MainActivity::class.java)
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(returnIntent)

        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}