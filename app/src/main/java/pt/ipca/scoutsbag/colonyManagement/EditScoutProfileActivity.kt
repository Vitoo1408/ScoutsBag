package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User

class EditScoutProfileActivity : AppCompatActivity() {

    lateinit var user: User
    private var profileImage: ImageView? = null
    private var editNin: EditText? = null
    private var checkBoxDirigente: CheckBox? = null
    private var buttonSave: Button? = null
    private var teamId: Int? = null
    private var userTeam: Team? = null

    // Global Variables
    var teams : MutableList<Team> = arrayListOf()
    //var selectedTeam: Team? = null
    var buttonTeamList: MutableList<Button> = arrayListOf()

    private var sectionImages: MutableList<ImageView> = arrayListOf()
    private lateinit var listViewTeams: ListView
    lateinit var adapter: TeamsAdapter

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

        listViewTeams = findViewById(R.id.listViewTeams)
        adapter = TeamsAdapter()
        listViewTeams.adapter = adapter

        // Add activity type images to the list
        sectionImages.add(findViewById(R.id.imageViewLobitos2))
        sectionImages.add(findViewById(R.id.imageViewExploradores2))
        sectionImages.add(findViewById(R.id.imageViewPioneiros2))
        sectionImages.add(findViewById(R.id.imageViewCaminheiros2))

        // On click section image
        for (image in sectionImages)
            image.setOnClickListener(onClickSection)

        GlobalScope.launch(Dispatchers.IO) {
            userTeam = Backend.getTeam(teamId!!)

            GlobalScope.launch(Dispatchers.Main) {
                // Configure the section image and the buttons list
                onClickSection(sectionImages[userTeam?.idSection!! - 1])
            }
        }

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

    // This function is for select an section by clicking on the section image
    private var onClickSection: (view: View)->Unit = {

        // Variables
        val imageView = it as ImageView
        val sectionId: Int = getSectionID(imageView.id)
        var buttonSpacing = 550

        for (i in 0 until sectionImages.size){
            sectionImages[i].setBackgroundResource(0)
            sectionImages[i].isHovered = false
            removeSectionTeams(i + 1)
        }

        // Add all teams of the selected section
        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllSectionTeams(sectionId) { list ->
                teams.addAll(list)
            }

            // Refresh the list
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

        imageView.isHovered = true
        imageView.setBackgroundResource(R.drawable.border)
        // Inactivate accept button
        println("bloqueado")
        buttonSave!!.setBackgroundResource(R.drawable.custom_button_white)
        buttonSave!!.isClickable = false


        // Refresh the listView size
        val params: ViewGroup.LayoutParams = listViewTeams.layoutParams
        params.height = buttonSpacing
        listViewTeams.layoutParams = params
        listViewTeams.requestLayout()
    }


    // This function is for select an team by clicking on the team button
    var onClickTeam: (view: View)->Unit = {
        val button = it as Button
        val team = findTeamByItsButton(button)

        // Unselect buttons
        for (i in 0 until buttonTeamList.size){
            buttonTeamList[i].setBackgroundResource(R.drawable.custom_button_white)
            buttonTeamList[i].setTextColor(Color.BLACK)
        }

        // Select button
        button.setBackgroundResource(R.drawable.custom_button_orange)
        button.setTextColor(Color.WHITE)
        // Activate accept button
        println("desbloqueado")
        buttonSave!!.setBackgroundResource(R.drawable.custom_button_orange)
        buttonSave!!.isClickable = true
        userTeam = team
        teamId = userTeam?.idTeam
    }

    /*
        This function return the section id on the data base depending on the imageView id
        @imageViewId = selected imageView id
     */
    private fun getSectionID(imageViewId: Int): Int {

        return when(imageViewId) {
            R.id.imageViewLobitos2 -> 1
            R.id.imageViewExploradores2 -> 2
            R.id.imageViewPioneiros2 -> 3
            else -> 4
        }

    }

    /*
        This function return a team depending on the selected team button
        @button = Selected team button
     */
    private fun findTeamByItsButton(button: Button): Team {

        var team: Team? = null

        for (i in 0 until teams.size) {
            if (teams[i].teamName == button.text)
                team = teams[i]
        }

        return team!!
    }

    inner class TeamsAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return teams.size
        }

        override fun getItem(position: Int): Any {
            return teams[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_team, parent, false)

            // Get current activity
            val teamButton = rowView.findViewById<Button>(R.id.buttonTeam)

            teamButton.text = teams[position].teamName
            teamButton.setOnClickListener(onClickTeam)
            buttonTeamList.add(teamButton)

            // See if the team is already selected
            if (teams[position].idTeam == userTeam?.idTeam && !teamButton.isHovered) {
                onClickTeam(teamButton)
            }

            return rowView
        }
    }

    /*
        This function all the teams of an selected section from the list
        @idSection = selected section
     */
    private fun removeSectionTeams(idSection: Int) {

        // Find the teams of the selected section
        for (i in teams.size-1 downTo 0) {
            if (teams[i].idSection == idSection)
                teams.removeAt(i)
        }

        adapter.notifyDataSetChanged()
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}