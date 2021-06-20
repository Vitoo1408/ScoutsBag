package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.notify
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

        // Get all sections
        val sections: MutableList<Section> = arrayListOf()
        for (i in 0 until 4)
            sections.add(Section(i, false))

        // Set values in the view
        editTextActivityName.text.append(activity.nameActivity)
        editTextActivityDescription.text.append(activity.activityDescription)
        dateStartButton.text = Utils.changeDateFormat(Utils.mySqlDateToString(activity.startDate!!)) + " - " + Utils.mySqlTimeToString(activity.startDate!!)
        dateEndButton.text = Utils.changeDateFormat(Utils.mySqlDateToString(activity.finishDate!!)) + " - " + Utils.mySqlTimeToString(activity.finishDate!!)
        editTextActivityPrice.text.append(activity.price.toString())
        editTextActivityLocalization.text.append(activity.activitySite)
        editTextActivityLocalizationStart.text.append(activity.startSite)
        editTextActivityLocalizationEnd.text.append(activity.finishSite)
        addButton.text = "Editar Atividade"

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

                       // Get all team from the section
                       GlobalScope.launch(Dispatchers.IO) {
                           Backend.getAllSectionTeams(section) { list ->
                               teams.addAll(list)
                           }

                           GlobalScope.launch(Dispatchers.Main) {
                               teamAdapter.notifyDataSetChanged()
                           }
                       }

                       // Configure the section image and the buttons list
                       sectionImage.isHovered = !sectionImage.isHovered
                       sectionImage.setBackgroundResource(R.drawable.border)
                       val params: ViewGroup.LayoutParams = listViewTeams.layoutParams
                       params.height = params.height + 600
                       listViewTeams.layoutParams = params
                       listViewTeams.requestLayout()
                   }
               }
            }
        }



        // Select the activity type already selected
        onClickActivityType(activityTypesImages[activity.idType!!-1])

        // Edit button
        addButton.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {

                // Build the activity that will be added
                val scoutActivity = ScoutActivity(
                    activityId,
                    editTextActivityName.text.toString(),
                    getSelectedActivityType(),
                    editTextActivityDescription.text.toString(),
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

                // Delete all invited teams
                Backend.removeAllInvitedTeams(activityId!!)

                // Get and then Delete all invited users
                var oldInvites: List<Invite> = arrayListOf()
                Backend.getAllActivityInvites(activityId!!) { oldInvites = it }
                for (invite in oldInvites)
                    Backend.removeInvite(invite)

                // Get the new invites
                for (team in selectedTeams) {

                    // Create an ActivityTeam for this team
                    Backend.addActivityTeam(ActivityTeam(activityId!!, team.idTeam))

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

                        // Add invite to the data Base
                        Backend.addInvite(invite)
                    }
                }

                // Remove all previous activityMaterials
                var oldActivityMaterials: List<ActivityMaterial> = arrayListOf()
                Backend.getAllRequestedActivityMaterial(activityId!!) { oldActivityMaterials = it}
                for (activityMaterial in oldActivityMaterials) {
                    Backend.removeActivityMaterial(activityMaterial)
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