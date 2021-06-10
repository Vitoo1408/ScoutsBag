package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.activityManagement.CreateActivityActivity
import pt.ipca.scoutsbag.models.Invite
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User

class ActivityReplyRequest : AppCompatActivity() {

    // Global Variables
    lateinit var user: User
    var id = ""
    var teams : MutableList<Team> = arrayListOf()
    var selectedTeam: Team? = null
    var buttonTeamList: MutableList<Button> = arrayListOf()
    var correctDate : String? = null
    var correctTime : String? = null
    var correctDateTime : String? = null
    private var sectionImages: MutableList<ImageView> = arrayListOf()
    private lateinit var listViewTeams: ListView
    lateinit var adapter: ActivityReplyRequest.TeamsAdapter

    var buttonAcceptUser : Button? = null


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
        buttonAcceptUser!!.setBackgroundResource(R.drawable.custom_button_white)
        buttonAcceptUser!!.isClickable = false


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
        buttonAcceptUser!!.setBackgroundResource(R.drawable.custom_button_orange)
        buttonAcceptUser!!.isClickable = true
        selectedTeam = team

    }

    // This function is for return to the previous activity after a operation
    var changeActivity: ()->Unit = {
        val returnIntent = Intent(this, MainActivity::class.java)
        startActivity(returnIntent)
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply_request)

        // Get the user from the last activity
        val userJsonStr = intent.getStringExtra("user")!!
        user = User.fromJson(JSONObject(userJsonStr))

        listViewTeams = findViewById(R.id.listViewTeams)
        adapter = TeamsAdapter()
        listViewTeams.adapter = adapter

        // Add activity type images to the list
        sectionImages.add(findViewById(R.id.imageViewLobitos))
        sectionImages.add(findViewById(R.id.imageViewExploradores))
        sectionImages.add(findViewById(R.id.imageViewPioneiros))
        sectionImages.add(findViewById(R.id.imageViewCaminheiros))

        // On click section image
        for (image in sectionImages)
            image.setOnClickListener(onClickSection)


        // Variables
        val textName = findViewById<TextView>(R.id.textName)
        val textNIN = findViewById<TextView>(R.id.textNIN)
        val textPhone = findViewById<TextView>(R.id.textPhone)
        val textMail = findViewById<TextView>(R.id.textMail)
        val textBirthDate = findViewById<TextView>(R.id.textBirthDate)
        val textAddress = findViewById<TextView>(R.id.textAddress)
        val textPostalCode = findViewById<TextView>(R.id.textPostalCode)

        buttonAcceptUser = findViewById(R.id.buttonAcceptUser)
        val buttonRefuseUser = findViewById<Button>(R.id.buttonRefuseUser)




        // Set data
        textName.text = user.userName
        textNIN.text = user.nin
        textPhone.text = user.contact
        textMail.text = user.email
        textBirthDate.text = Utils.mySqlDateTimeToString(user.birthDate!!)
        textAddress.text = user.address
        textPostalCode.text = user.postalCode

        // Set correct date time format
        println("DATA BD " + user.birthDate)
        correctDate = Utils.mySqlDateToString(user.birthDate!!)
        correctTime = Utils.mySqlTimeToString(user.birthDate!!)
        println("DATA INCoRRETA " + correctDate)
        println("TEMPO INCoRRETA " + correctTime)
        correctDateTime = Utils.changeDateFormat(correctDate!!) + " - " + correctTime

        println("DATA CoRRETA " + correctDateTime)

        correctDateTime = Utils.dateTimeToMySql(correctDateTime!!)

        println("DATA CORRETISSIMA " + correctDateTime)


        // Edit user events
        buttonAcceptUser!!.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {

                // Build the user that will be added
                val userUpdated = User(
                    user.idUser,
                    user.userName,
                    correctDateTime,
                    user.codType,
                    user.pass,
                    user.email,
                    user.contact,
                    user.gender,
                    user.address,
                    user.nin,
                    user.imageUrl,
                    user.postalCode,
                    1,
                    1,
                    selectedTeam!!.idTeam
                )

                // Edit user
                Backend.editUser(userUpdated, changeActivity)


            }

        }

        buttonRefuseUser.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {

                // Build the user that will be added
                val userUpdated = User(
                    user.idUser,
                    user.userName,
                    correctDateTime,
                    user.codType,
                    user.pass,
                    user.email,
                    user.contact,
                    user.gender,
                    user.address,
                    user.nin,
                    user.imageUrl,
                    user.postalCode,
                    0,
                    0,
                    user.idTeam
                )

                // Edit user
                Backend.editUser(userUpdated, changeActivity)

            }

        }


        // Inactivate accept button
        buttonAcceptUser!!.setBackgroundResource(R.drawable.custom_button_white)
        buttonAcceptUser!!.isClickable = false
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


    /*
        This function return the section id on the data base depending on the imageView id
        @imageViewId = selected imageView id
     */
    private fun getSectionID(imageViewId: Int): Int {

        return when(imageViewId) {
            R.id.imageViewLobitos -> 1
            R.id.imageViewExploradores -> 2
            R.id.imageViewPioneiros -> 3
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

            return rowView
        }
    }
}