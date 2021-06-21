package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.*


class CreateActivityActivity : ScoutActivityCreationHelper() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        onClickActivityType(findViewById(R.id.imageViewActivityType1))

        //check internet connection
        Utils.connectionLiveData(this)

        // Set actionbar title
        supportActionBar?.title = "Criar atividade"

        // Interact with the data base
        GlobalScope.launch(Dispatchers.IO) {

            // Get the new activity ID
            activityId = Backend.getLastActivityId() + 1

            // Get all materials
            Backend.getAllMaterials {
                materials = it
            }
        }

        // Add activity and invite teams button event
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

                // Add activity
                Backend.addActivity(scoutActivity, changeActivity)

                // Invite all users selected to this activity
                for (team in selectedTeams) {

                    // Get all users from the current team
                    var teamUsers: List<User> = arrayListOf()
                    Backend.getAllTeamUsers(team.idTeam!!) {
                        teamUsers = it
                    }

                    // Create the team invite to activity
                    Backend.addActivityTeam(
                        ActivityTeam(activityId, team.idTeam)
                    )

                    // Create invites for all users in the team
                    for (user in teamUsers) {

                        // Build the invite
                        val invite = Invite(
                            activityId,
                            user.idUser,
                            null
                        )

                        // Create invite
                        Backend.addInvite(invite)
                    }
                }

                // Create the material request for the activity
                for (activityMaterial in selectedMaterials) {
                    Backend.addActivityMaterial(activityMaterial)
                }

            }

        }

    }


}