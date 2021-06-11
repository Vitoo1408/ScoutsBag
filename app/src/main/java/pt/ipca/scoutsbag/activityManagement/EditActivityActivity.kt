package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.*

class EditActivityActivity : ScoutActivityCreationHelper() {

    // Global Variables
    private lateinit var activity: ScoutActivity

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)

        // Set actionbar title
        supportActionBar?.title = "Editar atividade"

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr!!)
        activity = ScoutActivity.fromJson(activityJson)
        activityId = activity.idActivity

        // Variables in the view
        val editViewActivityName = findViewById<TextView>(R.id.editTextActivityName)
        val editViewActivityDescription = findViewById<TextView>(R.id.editTextActivityDescription)
        val dateStartButton = findViewById<TextView>(R.id.dateStartButton)
        val dateEndButton = findViewById<TextView>(R.id.dateEndButton)
        val editTextActivityPrice = findViewById<TextView>(R.id.editTextActivityPrice)
        val editTextActivityLocalization = findViewById<TextView>(R.id.editTextActivityLocalization)
        val editTextActivityLocalizationStart = findViewById<TextView>(R.id.editTextActivityLocalizationStart)
        val editTextActivityLocalizationEnd = findViewById<TextView>(R.id.editTextActivityLocalizationEnd)
        val buttonEdit = findViewById<TextView>(R.id.buttonAddActivity)
        val buttonMaterial = findViewById<TextView>(R.id.buttonMaterial)

        // Set values in the view
        editViewActivityName.text = activity.nameActivity
        editViewActivityDescription.text = activity.activityDescription
        dateStartButton.text = Utils.mySqlDateTimeToString(activity.startDate!!)
        dateEndButton.text = Utils.mySqlDateTimeToString(activity.finishDate!!)
        editTextActivityPrice.text = activity.price.toString()
        editTextActivityLocalization.text = activity.activitySite
        editTextActivityLocalizationStart.text = activity.startSite
        editTextActivityLocalizationEnd.text = activity.finishSite
        buttonEdit.text = "Editar Atividade"

        // Get all materials from this activity from data base
        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllMaterials {
                materials = it
            }

            Backend.getAllActivityMaterial(activity.idActivity!!) {

                for (i in it.indices) {
                    val activityMaterial = ActivityMaterial(
                        activity.idActivity,
                        it[i].idMaterial,
                        it[i].qntStock
                    )
                    selectedMaterials.add(activityMaterial)
                }

                GlobalScope.launch(Dispatchers.Main) {
                    buttonMaterial.text = "Itens selecionados: ${selectedMaterials.size}"
                }
            }
        }

        // Get all sections
        val sections: MutableList<Section> = arrayListOf()
        for (i in 0 until 4)
            sections.add(Section(i, false))

        GlobalScope.launch(Dispatchers.IO) {

            // Get all previous invited teams
            Backend.getAllInvitedTeams(activity.idActivity!!) { list ->
               selectedTeams.addAll(list)
            }

            // Verify if the section is already displayed
            for (i in 0 until selectedTeams.size) {

               // Get the team section
               val section = selectedTeams[i].idSection!!
               val teamSection = sections[section-1]

               // Select if its not already selected
               if (!teamSection.active!!) {
                   teamSection.active = true
                   GlobalScope.launch(Dispatchers.Main) {

                       // Select the corresponding section
                       val sectionImage: ImageView = when (section) {
                           1 -> findViewById(R.id.imageViewLobitos)
                           2 -> findViewById(R.id.imageViewExploradores)
                           3 -> findViewById(R.id.imageViewPioneiros)
                           else -> findViewById(R.id.imageViewCaminheiros)
                       }

                       onClickSection(sectionImage)
                   }
               }
            }
        }

        // Select the activity type already selected
        onClickActivityType(activityTypesImages[activity.idType!!-1])

        // Edit button
        buttonEdit.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {

                // Build the activity that will be added
                val scoutActivity = ScoutActivity(
                    activityId,
                    editViewActivityName.text.toString(),
                    getSelectedActivityType(),
                    editViewActivityDescription.text.toString(),
                    editTextActivityLocalization.text.toString(),
                    Utils.dateTimeToMySql(dateStartButton.text.toString()),
                    Utils.dateTimeToMySql(dateEndButton.text.toString()),
                    editTextActivityLocalization.text.toString(),
                    editTextActivityLocalizationStart.text.toString(),
                    editTextActivityLocalizationEnd.text.toString(),
                    editTextActivityPrice.text.toString().toFloat()
                )

                // Edit activity
                Backend.editActivity(scoutActivity)

                // Variables
                val newInvites: MutableList<Invite> = arrayListOf()
                val previousInvites: MutableList<Invite> = arrayListOf()

                // Get the previous invites
                Backend.getAllActivityInvites(activityId!!) {
                    previousInvites.addAll(it)
                }

                // Get the new invites
                for (team in selectedTeams) {

                    // Get all users from the current team
                    var teamUsers: List<User> = arrayListOf()
                    Backend.getAllTeamUsers(team.idTeam!!) {
                        teamUsers = it
                    }

                    // Create invites for all users in the team
                    for (user in teamUsers) {

                        // Build the invite
                        val invite = Invite(
                            activityId,
                            user.idUser,
                            null
                        )
                        // Add invite to the list and to the data Base
                        newInvites.add(invite)
                        Backend.addInvite(invite)
                    }
                }

                // Look if any invite have been removed during the edition, and the remove them from the dataBase
                for (i in 0 until previousInvites.size) {
                    var found = false
                    for (j in 0 until newInvites.size) {
                        if (previousInvites[i].idUser == newInvites[i].idUser) {
                            found = true
                        }
                    }

                    // Remove it if not found
                    if (!found)
                        Backend.removeInvite(previousInvites[i])
                }

                // Remove all previous activityMaterials
                for (activityMaterial in selectedMaterials) {
                    Backend.removeActivityMaterial(activityId!!)
                }

                // Add all new activityMaterials
                for (activityMaterial in selectedMaterials) {
                    Backend.addActivityMaterial(activityMaterial)
                }

                // Change to the previous activity
                changeActivity()
            }
        }

    }

}